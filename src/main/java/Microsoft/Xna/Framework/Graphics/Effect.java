package Microsoft.Xna.Framework.Graphics;

import org.openeggbert.cna.internal.NativeBindings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** XNA Effect backed by CNA's compiled-effect and reflection ABI. */
@SuppressWarnings("this-escape")
public class Effect extends GraphicsResource {

    private final Map<Integer, EffectTechnique> techniquesByIndex = new HashMap<>();
    private EffectParameterCollection parameters;
    private EffectTechniqueCollection techniques;
    private EffectTechnique currentTechnique;

    public Effect(GraphicsDevice graphicsDevice, int[] effectCode) {
        super(Objects.requireNonNull(graphicsDevice, "graphicsDevice"));
        Objects.requireNonNull(effectCode, "effectCode");
        if (effectCode.length == 0) {
            throw new IllegalArgumentException("Effect bytecode must not be empty");
        }
        byte[] code = new byte[effectCode.length];
        for (int index = 0; index < effectCode.length; index++) {
            int value = effectCode[index];
            if ((value & ~0xff) != 0) {
                throw new IllegalArgumentException(
                        "Effect bytecode values must be between 0 and 255");
            }
            code[index] = (byte)value;
        }
        NativeBindings.createEffect(this, graphicsDevice, code, false);
    }

    protected Effect(Effect cloneSource) {
        super(Objects.requireNonNull(cloneSource, "cloneSource").getGraphicsDevice());
        cloneSource.requireEffectAlive();
        NativeBindings.cloneEffect(this, cloneSource);
    }

    Effect(GraphicsDevice graphicsDevice, boolean emptyNativeEffect) {
        super(Objects.requireNonNull(graphicsDevice, "graphicsDevice"));
        NativeBindings.createEffect(this, graphicsDevice, new byte[0], emptyNativeEffect);
    }

    Effect(GraphicsDevice graphicsDevice, String stockEffect) {
        super(Objects.requireNonNull(graphicsDevice, "graphicsDevice"));
        if (!"BasicEffect".equals(stockEffect)) {
            throw new IllegalArgumentException("Unknown stock effect " + stockEffect);
        }
        NativeBindings.createBasicEffect(this, graphicsDevice);
    }

    public Effect Clone() {
        requireEffectAlive();
        return new Effect(this);
    }

    public final EffectTechnique getCurrentTechnique() {
        requireEffectAlive();
        if (currentTechnique == null) {
            long[] current = NativeBindings.getEffectCurrentTechnique(this);
            if (current[0] == 0L) {
                return null;
            }
            currentTechnique = resolveTechnique(Math.toIntExact(current[1]), current[0]);
        }
        return currentTechnique;
    }

    public final void setCurrentTechnique(EffectTechnique value) {
        requireEffectAlive();
        Objects.requireNonNull(value, "value");
        if (value.getOwner() != this) {
            throw new IllegalArgumentException("EffectTechnique belongs to a different Effect");
        }
        value.requireAlive();
        if (currentTechnique == value) {
            return;
        }
        NativeBindings.setEffectCurrentTechnique(this, value);
        currentTechnique = value;
    }

    public final EffectParameterCollection getParameters() {
        requireEffectAlive();
        if (parameters == null) {
            parameters = new EffectParameterCollection(
                    this, NativeBindings.getEffectCollection(this, 0));
        }
        return parameters;
    }

    public final EffectTechniqueCollection getTechniques() {
        requireEffectAlive();
        if (techniques == null) {
            techniques = new EffectTechniqueCollection(
                    this, NativeBindings.getEffectCollection(this, 1));
        }
        return techniques;
    }

    protected void OnApply() {
        requireEffectAlive();
        NativeBindings.applyEffect(this);
    }

    @Override
    protected void Dispose(boolean arg0) {
        if (arg0 && !getIsDisposed()) {
            NativeBindings.closeEffectMembers(this);
            NativeBindings.closeGraphicsResource(this);
        }
        super.Dispose(arg0);
    }

    final EffectTechnique resolveTechnique(int index, long nativeHandle) {
        EffectTechnique existing = techniquesByIndex.get(index);
        if (existing != null) {
            if (nativeHandle != 0L) {
                NativeBindings.releaseDuplicateEffectMember(nativeHandle, 6);
            }
            return existing;
        }
        if (nativeHandle == 0L) {
            throw new IllegalStateException("CNA returned an invalid EffectTechnique handle");
        }
        EffectTechnique created = new EffectTechnique(this, index, nativeHandle);
        techniquesByIndex.put(index, created);
        return created;
    }

    final EffectTechnique cachedTechnique(int index) {
        return techniquesByIndex.get(index);
    }

    final void requireEffectAlive() {
        ensureNotDisposed();
    }
}

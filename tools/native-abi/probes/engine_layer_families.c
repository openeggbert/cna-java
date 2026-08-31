/* Which engine-layer families are usable in this HEADLESS/HEADLESS build?
   The layer reports itself available, but "available" and "does anything without a GPU" are
   different questions, and binding a family that answers NOT_SUPPORTED to everything would be
   shipping an API nobody can call. */
#include <CNA/C/engine_layer.h>
#include <CNA/C/graphics_ext.h>
#include <stdio.h>

static const char* name_of(const CNA_Result result)
{
    switch ((int)result) {
        case 0: return "SUCCESS";
        case 1: return "INVALID_ARGUMENT";
        case 2: return "INVALID_HANDLE";
        case 3: return "INVALID_STATE";
        case 6: return "NOT_SUPPORTED";
        default: return "OTHER";
    }
}

#define TRY(label, expression)                                                 \
    do {                                                                       \
        const CNA_Result probe_result = (expression);                          \
        printf("%-34s %-16s (%d)\n", label, name_of(probe_result),             \
               (int)probe_result);                                             \
    } while (0)

int main(void)
{
    CNA_Bool available = CNA_FALSE;
    int32_t version = 0;
    printf("graphics_ext_is_available    %d\n",
           (int)(cna_graphics_ext_is_available(&available) == CNA_RESULT_SUCCESS && available));
    TRY("engine_layer_get_version", cna_engine_layer_get_version(&version));
    printf("engine layer version         %d (header %d)\n",
           (int)version, (int)CNA_ENGINE_LAYER_VERSION);

    {
        CNA_DebugDrawHandle debug = 0;
        TRY("debug_draw_create", cna_debug_draw_create(CNA_INVALID_HANDLE, &debug));
        if (debug != 0) {
            TRY("debug_draw_destroy", cna_debug_draw_destroy(debug));
        }
    }
    {
        CNA_LodGroupEXTHandle lod = 0;
        TRY("lod_group_create", cna_lod_group_ext_create(&lod));
        if (lod != 0) {
            TRY("lod_group_ext_destroy", cna_lod_group_ext_destroy(lod));
        }
    }
    {
        CNA_ParticleSystemHandle particles = 0;
        TRY("particle_system_create", cna_particle_system_create(CNA_INVALID_HANDLE, &particles));
        if (particles != 0) {
            TRY("particle_system_destroy", cna_particle_system_destroy(particles));
        }
    }
    return 0;
}

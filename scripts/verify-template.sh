#!/usr/bin/env bash
set -euo pipefail

binding_root=$(CDPATH= cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
template_root=${CNA_JAVA_TEMPLATE_ROOT:-"$binding_root/../cna-java-template"}

if [[ ! -x "$template_root/gradlew" ]]; then
    echo "Template Gradle Wrapper not found: $template_root/gradlew" >&2
    exit 2
fi

verification_root=$(mktemp -d "${TMPDIR:-/tmp}/cna-java-template-verify.XXXXXX")
repository="$verification_root/maven"
generated="$verification_root/generated"

cleanup() {
    if [[ -n "${verification_root:-}" && -d "$verification_root" ]]; then
        rm -rf -- "$verification_root"
    fi
}
trap cleanup EXIT

echo "[1/4] Building CNA-Java and publishing to $repository"
(
    cd "$binding_root"
    ./gradlew --no-daemon clean check publishToMavenLocal \
        "-Dmaven.repo.local=$repository"
)

echo "[2/4] Building sibling template against the exact temporary artifact"
(
    cd "$template_root"
    ./gradlew --no-daemon clean test installDist "-PcnaRepository=$repository"
)

echo "[3/4] Generating and building a standalone project"
python3 "$template_root/scripts/generate_project.py" \
    --output "$generated" \
    --project-name "Verification Game" \
    --package org.openeggbert.verification \
    --application-id org.openeggbert.verification.desktop \
    --game-class VerificationGame \
    --group org.openeggbert.verification \
    --artifact-id cna-java-generated-verification
(
    cd "$generated"
    ./gradlew --no-daemon clean test installDist "-PcnaRepository=$repository"
)

echo "[4/4] Native execution"
if [[ -n "${CNA_NATIVE_LIBRARY:-}" ]]; then
    case "$(uname -s)" in
        Linux*) jni_library="$binding_root/build/native/libcna_java_jni.so" ;;
        Darwin*) jni_library="$binding_root/build/native/libcna_java_jni.dylib" ;;
        MINGW*|MSYS*|CYGWIN*) jni_library="$binding_root/build/native/cna_java_jni.dll" ;;
        *) echo "Unsupported host for JNI test: $(uname -s)" >&2; exit 2 ;;
    esac
    (
        cd "$template_root"
        CNA_JNI_LIBRARY="$jni_library" ./gradlew --no-daemon :game:run \
            "-PcnaRepository=$repository" --args=--smoke-test
    )
    if [[ "${CNA_RUN_STABILITY_TEST:-0}" == "1" ]]; then
        (
            cd "$template_root"
            CNA_JNI_LIBRARY="$jni_library" ./gradlew --no-daemon :game:run \
                "-PcnaRepository=$repository" --args=--stability-test
        )
    else
        echo "Stability run skipped (set CNA_RUN_STABILITY_TEST=1 to execute 600 frames)."
    fi
else
    echo "Native run skipped (CNA_NATIVE_LIBRARY is not set)."
fi

echo "CNA-Java/template verification passed without using the global Maven repository."

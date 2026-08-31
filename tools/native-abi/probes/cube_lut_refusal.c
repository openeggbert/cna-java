/* JAVA-UPSTREAM-009, reproduced with no Java anywhere in the picture.

   `cna_cube_lut_parse` documents `CNA_RESULT_INVALID_ARGUMENT` for text the parser refuses --
   a missing size, a malformed domain line, an entry count that disagrees with the declared size
   -- and the route's own body catches `CNA::CNAException` to return exactly that. The parser
   throws `CNA::Graphics::EngineException` instead, which that catch does not name and which the
   surrounding exception barrier maps to `CNA_RESULT_NOT_SUPPORTED`. So a typo in an artist's
   `.cube` file arrives at a game as "this renderer cannot do colour grading", and a game that
   catches the capability refusal to fall back will fall back for a file it could have rejected.

   Needs no device: parsing is text. */
#include <CNA/C/engine_layer.h>
#include <CNA/C/core.h>
#include <stdio.h>
#include <string.h>

static const char* name_of(const CNA_Result result)
{
    switch ((int)result) {
        case 0: return "SUCCESS";
        case 1: return "INVALID_ARGUMENT";
        case 6: return "NOT_SUPPORTED";
        case 11: return "ENCODING";
        default: return "OTHER";
    }
}

static CNA_Result parse(const char* text)
{
    CNA_StringView view;
    view.data = text;
    view.byte_length = strlen(text);
    CNA_CubeLutHandle lut = 0;
    const CNA_Result result = cna_cube_lut_parse(view, &lut);
    if (lut != 0) {
        (void)cna_cube_lut_destroy(lut);
    }
    return result;
}

int main(void)
{
    static const char* const kGood =
        "LUT_3D_SIZE 2\n"
        "0 0 0\n0 0 1\n0 1 0\n0 1 1\n1 0 0\n1 0 1\n1 1 0\n1 1 1\n";
    static const char* const kNoSize = "DOMAIN_MIN 0 0 0\n";
    static const char* const kShort = "LUT_3D_SIZE 2\n0 0 0\n";
    static const char* const kBadDomain = "LUT_3D_SIZE 2\nDOMAIN_MIN nonsense\n";

    printf("well formed            %s\n", name_of(parse(kGood)));

    const CNA_Result missing = parse(kNoSize);
    const CNA_Result truncated = parse(kShort);
    const CNA_Result malformed = parse(kBadDomain);
    printf("no LUT_3D_SIZE         %s (%d)\n", name_of(missing), (int)missing);
    printf("too few entries        %s (%d)\n", name_of(truncated), (int)truncated);
    printf("malformed domain line  %s (%d)\n", name_of(malformed), (int)malformed);
    printf("header documents       INVALID_ARGUMENT (1)\n");

    if (missing == CNA_RESULT_INVALID_ARGUMENT && truncated == CNA_RESULT_INVALID_ARGUMENT
        && malformed == CNA_RESULT_INVALID_ARGUMENT) {
        printf("\nRESULT: the header and the library agree; JAVA-UPSTREAM-009 is fixed\n");
        return 0;
    }
    if (missing == CNA_RESULT_NOT_SUPPORTED && truncated == CNA_RESULT_NOT_SUPPORTED
        && malformed == CNA_RESULT_NOT_SUPPORTED) {
        printf("\nRESULT: JAVA-UPSTREAM-009 reproduced -- a malformed file is reported as a\n"
               "        missing capability, so a game falls back instead of rejecting it\n");
        return 0;
    }
    printf("\nRESULT: neither -- the refusals changed\n");
    return 1;
}

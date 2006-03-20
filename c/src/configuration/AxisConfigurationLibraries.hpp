#include "AxisConfiguration.hpp"

const char * CreateConfigElement( LIST * psDLLNames, int * piConfigInfoArray, CHOICELIST * psChoiceList, ECONFIGTYPE eConfigType);
void Initialise( LIST * psDLLNames, int * piConfigInfoArray, LIST * psFileNameList, char ** ppsDefaultParamList);
void Destroy( LIST * psDLLNames, LIST * psFileNameList, char ** psDefaultParamList);
int PopulateNewDLLNameInfo( LIST * psDLLNames, char * pszName, char * pszFilename, bool bAddToClientConfig);

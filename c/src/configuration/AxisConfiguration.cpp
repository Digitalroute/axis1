#include "AxisConfiguration.hpp"

int main( int argc, char * argv[])
{
	DLLNAMES		sDLLNames;
	int				iConfigInfoArray[eConfigMax];
	CHOICELIST		sChoiceList[] = { {PLATFORM_TRANSPORTHTTP_PATH,	"HTTP Transport library",											AXCONF_TRANSPORTHTTP_TAGNAME,		eHTTPTransport,		eClientAndServer},
									  {PLATFORM_CHANNEL_PATH,		"HTTP Channel library",												AXCONF_CHANNEL_HTTP_TAGNAME,		eHTTPChannel,		eClientAndServer},
									  {"HTTPSSLCHANNEL",			"HTTP SSL Channel library",											AXCONF_SSLCHANNEL_HTTP_TAGNAME,		eHTTPSSLChannel,	eClientAndServer},
									  {PLATFORM_XMLPARSER_PATH,		"Axis XML Parser library",											AXCONF_XMLPARSER_TAGNAME,			eXMLParser,			eClientAndServer},
									  {"SMTPTRANSPORT",				"SMTP Transport library",											AXCONF_TRANSPORTSMTP_TAGNAME,		eSMTPTransport,		eClientAndServer},
									  {"LOG",						"client trace log path (only required if you want client trace)",	AXCONF_CLIENTLOGPATH_TAGNAME,		eClientLog,			eClient},
									  {"WSDD",						"client WSDD path",													AXCONF_CLIENTWSDDFILEPATH_TAGNAME,	eClientWSDD,		eClient},
									  {"LOG",						"server trace log path (only required if you want server trace)",	AXCONF_LOGPATH_TAGNAME,				eServerLog,			eServer},
									  {"WSDD",						"server WSDD path",													AXCONF_WSDDFILEPATH_TAGNAME,		eServerWSDD,		eServer},
									  {"",							"",																	AXCONF_NODENAME_TAGNAME,			eUnknown,			eServer},
									  {"",							"",																	AXCONF_LISTENPORT_TAGNAME,			eUnknown,			eServer},
									  {"",							"",																	AXCONF_SECUREINFO_TAGNAME,			eUnknown,			eClientAndServer}};
	bool			bSuccess = false;
	FILENAMELIST	sFileNameList;

	Initialise( &sDLLNames, iConfigInfoArray, &sFileNameList);

	switch( ReadConfigOptions( argc, argv))
	{
		case eEmpty:
		{
			cout << "Usage:" << endl;
			cout << "Client\tConfigure the client side." << endl;
			cout << "Server\tConfigure the server side." << endl;
			cout << "Both\tConfigure the client and server side." << endl;
			break;
		}

		case eClient:
		{
			char	szAxisCpp_Deploy[256];
			char	szAxis_Bin[256];
			char	szAxis_Bin_Default[256];

			cout << "Axis Client Configuration" << endl;
			cout << "=========================" << endl;

			GetHomeAndLibrary( &sDLLNames, szAxisCpp_Deploy, szAxis_Bin, szAxis_Bin_Default, &sFileNameList);

			int	iChoiceCount = 0;

			while( sChoiceList[iChoiceCount].eConfigType != eUnknown)
			{
				if( sChoiceList[iChoiceCount].eConfig & eClient)
				{
					if( sChoiceList[iChoiceCount].eConfigType == eClientLog ||
						sChoiceList[iChoiceCount].eConfigType == eServerLog)
					{
						if(sChoiceList[iChoiceCount].eConfigType == eClientLog)
						{
							cout << "Enter name of client trace/log file: ";
						}
						else
						{
							cout << "Enter name of server trace/log file: ";
						}

						cin >> sDLLNames.ppsDLLName[iChoiceCount]->pszDLLName;

						sprintf( sDLLNames.ppsDLLName[iChoiceCount]->pszDLLFilename, "%s\\%s", szAxisCpp_Deploy, sDLLNames.ppsDLLName[iChoiceCount]->pszDLLName);

						iConfigInfoArray[sChoiceList[iChoiceCount].eConfigType] = iChoiceCount;

						sDLLNames.ppsDLLName[iChoiceCount]->bAddToClientConfig = true;
					}
					else
					{
						SelectFileFromList( sChoiceList, iChoiceCount, &sDLLNames, iConfigInfoArray);
					}
				}

				iChoiceCount++;
			}

			bSuccess = true;

			break;
		}

		case eServer:
		{
			char	szAxisCpp_Deploy[256];
			char	szAxis_Bin[256];
			char	szAxis_Bin_Default[256];

			cout << "Axis Server Configuration" << endl;
			cout << "=========================" << endl;

			GetHomeAndLibrary( &sDLLNames, szAxisCpp_Deploy, szAxis_Bin, szAxis_Bin_Default, &sFileNameList);

			bSuccess = true;

			break;
		}

		case eClientAndServer:
		{
			cout << "Axis Client and Server Configuration" << endl;

			bSuccess = true;
			break;
		}
	}

	if( bSuccess)
	{
		WriteAxisConfigFile( &sDLLNames, iConfigInfoArray, sChoiceList);
	}

	Destroy( &sDLLNames, &sFileNameList);

	return (int) bSuccess;
}

ECONFIG	ReadConfigOptions( int iParamCount, char * pParamArray[])
{
	ECONFIG	eConfig = eEmpty;

	for( int iCount = 0; iCount < iParamCount; iCount++)
	{
		if( !stricmp( pParamArray[iCount], "Client"))
		{
			eConfig = (ECONFIG)((int) eConfig | eClient);
		}
		else if( !stricmp( pParamArray[iCount], "Server"))
		{
			eConfig = (ECONFIG)((int) eConfig | eServer);
		}
		else if( !stricmp( pParamArray[iCount], "Both"))
		{
			eConfig = (ECONFIG)((int) eConfig | eClientAndServer);
		}
	}

	return eConfig;
}
bool CheckAxisBinDirectoryExists( char * pszAxisCpp_Deploy, char * pszAxis_Bin, char * pszAxis_Bin_Default, DLLNAMES * psDLLNames, FILENAMELIST * psFileNameList)
{
	bool	bFound = false;

	if( strlen( pszAxis_Bin) < 2)
	{
		strcpy( pszAxis_Bin, pszAxis_Bin_Default);
	}

	char	szFilename[512];
	char	szFileDirAndName[512];

	sprintf( szFilename, "%s\\%s\\*.*", pszAxisCpp_Deploy, pszAxis_Bin);

	bFound = ReadFilenamesInaDirectory( szFilename, psFileNameList);

	int		iIndex = 0;

	do
	{
		char *	pExtn = psFileNameList->ppszListArray[iIndex];

		do
		{
			if( (pExtn = strchr( pExtn, '.')) != NULL)
			{
				pExtn++;

#if WIN32
				if( !stricmp( pExtn, "DLL"))
#else
				if( !stricmp( pExtn, "SO"))
#endif
				{
					if( psDLLNames->iIndex >= psDLLNames->iMaxCount)
					{
						psDLLNames->iMaxCount = psDLLNames->iMaxCount * 2 + 1;

						if( psDLLNames->ppsDLLName == NULL)
						{
							psDLLNames->ppsDLLName = (DLLNAMEINFO **) malloc( sizeof( void *) * psDLLNames->iMaxCount);
						}
						else
						{
							psDLLNames->ppsDLLName = (DLLNAMEINFO **) realloc( psDLLNames->ppsDLLName, sizeof( void *) * psDLLNames->iMaxCount);
						}

						for( int iNewIndex = psDLLNames->iIndex; iNewIndex < psDLLNames->iMaxCount; iNewIndex++)
						{
							psDLLNames->ppsDLLName[iNewIndex] = NULL;
						}
					}

					psDLLNames->ppsDLLName[psDLLNames->iIndex] = (DLLNAMEINFO *) malloc( sizeof( DLLNAMEINFO));

					memset( psDLLNames->ppsDLLName[psDLLNames->iIndex], 0, sizeof( DLLNAMEINFO));

					psDLLNames->ppsDLLName[psDLLNames->iIndex]->pszDLLName = (char *) malloc( strlen( psFileNameList->ppszListArray[iIndex]) + 1);

					strcpy( psDLLNames->ppsDLLName[psDLLNames->iIndex]->pszDLLName, psFileNameList->ppszListArray[iIndex]);

					strupr( psDLLNames->ppsDLLName[psDLLNames->iIndex]->pszDLLName);

					sprintf( szFileDirAndName, "%s\\%s\\%s", pszAxisCpp_Deploy, pszAxis_Bin, psFileNameList->ppszListArray[iIndex]);

					psDLLNames->ppsDLLName[psDLLNames->iIndex]->pszDLLFilename = (char *) malloc( strlen( szFileDirAndName) + 1);

					strcpy( psDLLNames->ppsDLLName[psDLLNames->iIndex]->pszDLLFilename, szFileDirAndName);

					psDLLNames->iIndex++;

					break;
				}
			}
		} while( pExtn != NULL);

		iIndex++;
	} while( iIndex < psFileNameList->iListCount);

	if( !bFound)
	{
		cout << "The directory " << pszAxis_Bin << " was not found." << endl;
	}

	return bFound;
}

const char * CreateConfigElement( DLLNAMES * psDLLNames, int * piConfigInfoArray, CHOICELIST * psChoiceList, ECONFIGTYPE eConfigType)
{
	int					iIndex = 0;
	bool				bFound = false;
	char *				pszConfigName = "<unknown name>";
	static std::string	sReturn;

	while( iIndex < eConfigMax && !bFound)
	{
		if( psChoiceList[iIndex].eConfigType == eConfigType)
		{
			bFound = true;

			pszConfigName = psChoiceList[iIndex].pszConfigName;
		}
		else
		{
			iIndex++;
		}
	}

	if( bFound)
	{
		sReturn = "#Path to ";
		sReturn += psChoiceList[iIndex].pszElementDescription;
		sReturn += "\n";
	}
	else
	{
		sReturn = "#Path to an unknown element\n";
	}

	char	szReturn[256];

	if( piConfigInfoArray[eConfigType] != -1)
	{
		sprintf( szReturn, "%s:%s\n", pszConfigName, psDLLNames->ppsDLLName[piConfigInfoArray[eConfigType]]->pszDLLFilename);
	}
	else
	{
		sprintf( szReturn, "#%s:<not set>\n", pszConfigName);
	}

	sReturn += szReturn;

	return sReturn.c_str();
}

void GetHomeAndLibrary( DLLNAMES * psDLLNames, char * pszAxisCpp_Deploy, char * pszAxis_Bin, char * pszAxis_Bin_Default, FILENAMELIST * psFileNameList)
{
#if WIN32
	cout << "Type in full qualified directory path into which you downloaded Axis." << endl
		 << "(e.g. C:\\Axis).  Where C:\\Axis\\axis-c-1.6-Win32-trace-bin is the" << endl
		 << "directory created when Axis was unzipped (this directory must also" << endl
		 << "contain the axiscpp.conf file)." << endl;
#else
	cout << "Type in full qualified directory path into which you downloaded Axis." << endl
		 << "(e.g. /home/Axis).  Where /home/Axis/axis-c-1.6-Linux-trace-bin is the" << endl
		 << "directory created when Axis was untared (this directory must also" << endl
		 << "contain the etc/axiscpp.conf file)." << endl;
#endif
	cout << "AXISCPP_DEPLOY = ";
	cin >> pszAxisCpp_Deploy;

	cout << endl << "Instruction:" << endl
		 << "You will need to create an environment variable called " << endl
		 << "\"AXISCPP_DEPLOY\" and set it to " << pszAxisCpp_Deploy << "." << endl
		 << "On the command line this would be:-" << endl
#if WIN32
		 << "SET AXISCPP_DEPLOY=" << pszAxisCpp_Deploy << endl << endl;
#else
		 << "EXPORT AXISCPP_DEPLOY=" << pszAxisCpp_Deploy << endl << endl;
#endif

	strcpy( pszAxis_Bin_Default, pszAxisCpp_Deploy);

#if WIN32
	strcat( pszAxis_Bin_Default, "\\axis-c-1.6-Win32-trace-bin\\bin");
#else
	strcat( pszAxis_Bin_Default, "/axis-c-1.6-Linux-trace-bin/bin");
#endif

	do
	{
#if WIN32
		cout << "Type in the directory where the Axis libraries (e.g. axis_client.dll) can be" << endl
				<< "found.  (The default directory is " << pszAxisCpp_Deploy << "\\axis-c-1.6-Win32-trace-bin\\bin)." << endl;
#else
		cout << "Type in the directory where the Axis libraries (e.g. axis_client.so) can be" << endl
				<< "found.  (The default directory is " << pszAxisCpp_Deploy << "/axis-c-1.6-Linux-trace-bin/bin)." << endl;
#endif
		cout << "Axis binaries directory = ";
		cin >> pszAxis_Bin;
	}
	while( !CheckAxisBinDirectoryExists( pszAxisCpp_Deploy, pszAxis_Bin, pszAxis_Bin_Default, psDLLNames, psFileNameList));

	cout << endl << "Begin to configure the AXISCPP.CONF file." << endl;
}

void Initialise( DLLNAMES * psDLLNames, int * piConfigInfoArray, FILENAMELIST * psFileNameList)
{
	memset( psDLLNames, 0, sizeof( DLLNAMES));
	memset( psFileNameList, 0, sizeof( FILENAMELIST));

	for( int iCount = 0; iCount < eConfigMax; iCount++)
	{
		piConfigInfoArray[iCount] = -1;
	}
}

void SelectFileFromList( CHOICELIST * psChoiceList, int iChoiceCount, DLLNAMES * psDLLNames, int * piConfigInfoArray)
{
	cout << endl << "Select the filename for the " << psChoiceList[iChoiceCount].pszElementDescription << "." << endl;

	int		iDLLCount = 0;
	int		iDLLListCount = 0;
	int		iDLLOffsetList[8];
	bool	bHTTPTransportFound = false;

	do
	{
		while( iDLLCount < psDLLNames->iIndex)
		{
			char *	pszUpper = new char[strlen( psChoiceList[iChoiceCount].pszElement) + 1];

			strcpy( pszUpper, psChoiceList[iChoiceCount].pszElement);
			strupr( pszUpper);

			if( strstr( psDLLNames->ppsDLLName[iDLLCount]->pszDLLName, pszUpper) != NULL)
			{
				iDLLOffsetList[iDLLListCount] = iDLLCount;

				cout << ++iDLLListCount << ".\t" << psDLLNames->ppsDLLName[iDLLCount]->pszDLLFilename << endl;
			}

			iDLLCount++;

			delete [] pszUpper;
		}

		if( iDLLListCount > 0)
		{
			if( iDLLListCount > 1)
			{
				cout << "Select an index between 1 and " << iDLLListCount << " : ";

				int	iChoice;

				cin >> iChoice;

				if( iChoice < 1 || iChoice > iDLLListCount)
				{
					cout << "Number was out of range." << endl;
				}
				else
				{
					psDLLNames->ppsDLLName[iDLLOffsetList[iChoice - 1]]->bAddToClientConfig = true;
					piConfigInfoArray[psChoiceList[iChoiceCount].eConfigType] = iDLLOffsetList[iChoice - 1];
					bHTTPTransportFound = true;
				}
			}
			else
			{
				cout << "Automatically selected " << psDLLNames->ppsDLLName[iDLLOffsetList[0]]->pszDLLFilename << endl;

				psDLLNames->ppsDLLName[iDLLOffsetList[0]]->bAddToClientConfig = true;
				piConfigInfoArray[psChoiceList[iChoiceCount].eConfigType] = iDLLOffsetList[0];
				bHTTPTransportFound = true;
			}
		}
		else
		{
			cout << "There are no recognised file names for the type of DLL." << endl << "You will have to modify the configuration file namually." << endl << endl;

			bHTTPTransportFound = true;
		}
	} while( !bHTTPTransportFound);
}

void WriteAxisConfigFile( DLLNAMES * psDLLNames, int * piConfigInfoArray, CHOICELIST * psChoiceList)
{
	cout << "DLL selection complete" << endl << endl;

	cout << "AxisCPP.conf file now has the following information:-" << endl;

	cout << "# The comment character is '#'" << endl;
	cout << "#Available directives are as follows" << endl;
	cout << "#(Some of these directives may not be implemented yet)" << endl;
	cout << "#" << endl;
	cout << CreateConfigElement( psDLLNames, piConfigInfoArray, psChoiceList, eServerLog) << endl;
	cout << CreateConfigElement( psDLLNames, piConfigInfoArray, psChoiceList, eServerWSDD) << endl;
	cout << CreateConfigElement( psDLLNames, piConfigInfoArray, psChoiceList, eClientLog) << endl;
	cout << CreateConfigElement( psDLLNames, piConfigInfoArray, psChoiceList, eClientWSDD) << endl;
	cout << "#Node name." << endl;
	cout << "#NodeName: <not set>" << endl << endl;
	cout << "#Listening port." << endl;
	cout << "#ListenPort: <not set>" << endl << endl;
	cout << CreateConfigElement( psDLLNames, piConfigInfoArray, psChoiceList, eHTTPTransport) << endl;
	cout << CreateConfigElement( psDLLNames, piConfigInfoArray, psChoiceList, eSMTPTransport) << endl;
	cout << CreateConfigElement( psDLLNames, piConfigInfoArray, psChoiceList, eXMLParser) << endl;
	cout << CreateConfigElement( psDLLNames, piConfigInfoArray, psChoiceList, eHTTPChannel) << endl;
	cout << CreateConfigElement( psDLLNames, piConfigInfoArray, psChoiceList, eHTTPSSLChannel) << endl;
	cout << endl;
}

bool ReadFilenamesInaDirectory( char * pszDirName, FILENAMELIST * psFileNameList)
{
	bool				bSuccess = false;

#if WIN32
	intptr_t			lFindFile;
	struct _finddata_t	sFindData;

	if( (lFindFile = _findfirst( pszDirName, &sFindData)) != -1)
	{
		if( sFindData.name[0] == '.' && sFindData.attrib & _A_SUBDIR)
		{
			bSuccess = true;
		}

		do
		{
			if( !(sFindData.attrib & _A_SUBDIR))
			{
				AddFilenameToList( psFileNameList, sFindData.name);
			}
		} while( _findnext( lFindFile, &sFindData) == 0);

		_findclose( lFindFile);
	}
#else
	DIR *				psDIR;
	struct dirent*		pDirEnt;
	int					iFilenameCount = 0;

	if( (pDir = opendir( pszDirName)) == NULL)
	{
		return bSuccess;
	}

	while( (pDirEnt = readdir( pDir)) != NULL)
	{
		AddFilenameToList( psFileNameList, pDirEnt->d_name);
	}

	if( closedir( pDir) == -1)
	{
		return bSuccess;
	}
	else
	{
		bSuccess = true;
	}
#endif

	return bSuccess;
}

void AddFilenameToList( FILENAMELIST * psFileNameList, char * pszFilename)
{
	if( psFileNameList->iListMax == 0)
	{
		psFileNameList->iListMax = 1;

		psFileNameList->ppszListArray = (char **) malloc( sizeof( char *));
	}
	else if( psFileNameList->iListCount >= psFileNameList->iListMax)
	{
		psFileNameList->iListMax *= 2;

		psFileNameList->ppszListArray = (char **) realloc( psFileNameList->ppszListArray, sizeof( char *) * psFileNameList->iListMax);
	}

	psFileNameList->ppszListArray[psFileNameList->iListCount] = (char *) malloc( strlen( pszFilename) + 1);

	strcpy( psFileNameList->ppszListArray[psFileNameList->iListCount], pszFilename);

	psFileNameList->iListCount++;
}

void Destroy( DLLNAMES * psDLLNames, FILENAMELIST * psFileNameList)
{
	int iCount;

	for( iCount = 0; iCount < psDLLNames->iIndex; iCount++)
	{
		delete [] psDLLNames->ppsDLLName[iCount]->pszDLLFilename;
		delete [] psDLLNames->ppsDLLName[iCount]->pszDLLName;
		delete [] psDLLNames->ppsDLLName[iCount];
	}

	for( iCount = 0; iCount < psFileNameList->iListCount; iCount++)
	{
		delete [] psFileNameList->ppszListArray[iCount];
	}

	delete [] psDLLNames->ppsDLLName;
	delete [] psFileNameList->ppszListArray;
}

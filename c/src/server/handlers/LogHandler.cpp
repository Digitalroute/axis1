//////////////////////////////////////////////////////////////////////

#include "LogHandler.h"
#include "../../soap/SoapDeSerializer.h"
#include "../../soap/SoapSerializer.h"
#include <fstream>
#include <string>
#include "../../common/Debug.h"

using namespace std;

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

LogHandler::LogHandler()
{
  m_Option = new map<string, string>;

}

LogHandler::~LogHandler()
{
  delete(m_Option);
  m_Option = NULL;

}
// Implementation of BasicHandler interface.
int LogHandler::Invoke(MessageData* md)
{
#if defined( DEBUG)
  debugger.debug("LogHandler::Invoke(MessageData* md)");
#endif
    m_iNumAccess = 0;
    string sNumAccess = "";
    string s = "logAccessCountFile";
    string sFileName = GetOption(s);

    if(!sFileName.empty())
    {
#if defined( DEBUG)
  debugger.debug("if(!sFileName.empty())");
#endif

      ifstream fin(sFileName.c_str());    // open for reading
       char ch;

       while (fin.get(ch))
       {
         sNumAccess += ch;

       }

      if(sNumAccess.empty())
      {
        m_iNumAccess = 0;
      }
      else
      {
        m_iNumAccess = atoi(sNumAccess.c_str());
      }

      m_iNumAccess++;
      sNumAccess = m_iNumAccess;

      const char * FileName = sFileName.c_str();
      ofstream fout(FileName);  // open for writing
      fout << m_iNumAccess;

      return SUCCESS;
    }
    else
    {
      return FAIL;
    }
}

void LogHandler::OnFault(MessageData* mc)
{

}


string LogHandler::GetOption(string sArg)
{
  return (*m_Option)[sArg];
}

void LogHandler::SetOption(string sOption, string sValue)
{
  (*m_Option)[sOption] = sValue;
}

void LogHandler::SetOptionList(map<string, string>* OptionList)
{
   m_Option = OptionList;
}

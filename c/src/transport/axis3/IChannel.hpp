#if !defined(_AXIS_ICHANNEL_HPP)
#define _AXIS_ICHANNEL_HPP
#include <string>
#include <axis/GDefine.hpp>
#include "URL.hpp"
#include "HTTPTransportException.hpp"

const int BUF_SIZE = 1024 * 8;

using namespace std;

AXIS_CPP_NAMESPACE_USE

class IChannel
{
public:
	virtual const char*			getURL()=0;
	virtual void				setURL( const char* cpURL)=0;
    virtual URL &				getURLObject()=0;
    virtual bool				open() throw (HTTPTransportException&)=0;
    virtual const std::string&	GetLastErrorMsg()=0;
//    virtual const IChannel&		operator >> (std::string& msg)=0;
    virtual const IChannel&		operator >> (const char * msg)=0;
    virtual const IChannel&		operator << (const char * msg)=0;
//	virtual void				setSecureProperties( const char *)=0;
//	virtual const char *		getSecureProperties()=0;
    virtual void				setTimeout( const long lSeconds)=0;
    virtual void				setSocket( unsigned int uiNewSocket)=0;
	virtual bool				setTransportProperty( AXIS_TRANSPORT_INFORMATION_TYPE type, const char* value)=0;
	virtual const char *		getTransportProperty( AXIS_TRANSPORT_INFORMATION_TYPE type)=0;
};

#endif


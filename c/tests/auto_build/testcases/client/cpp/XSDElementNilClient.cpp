
/*
 * Supported types are
  1 <element name="getBoolean">
  2 <element name="getByte">
  3 <element name="getUnsignedByte">
  4 <element name="getShort">
  5 <element name="getUnsignedShort">
  6 <element name="getInt">
  7 <element name="getUnsignedInt">
  8 <element name="getLong">
  9 <element name="getUnsignedLong">
 10 <element name="getFloat">
 11 <element name="getDouble">
 12 <element name="getDate">
 13 <element name="getDateTime">
 14 <element name="getTime">
 15 <element name="getDuration">
 16 <element name="getString">
 17 <element name="getInteger">
 18 <element name="getDecimal">
 19 <element name="getBase64Binary">
 20 <element name="getHexBinary">
 *
 */


#include "XSDElementNil.hpp"
#include <axis/AxisException.hpp>
#include <ctype.h>
#include <iostream>

#ifdef WIN32
  // Bug in MS Visual C++ 6.0. Fixed in Visual C++ .Net version.
  // Cannot print an __int64 number with cout without this overloading
  std::ostream& operator<<(std::ostream& os, __int64 i )
  {
    char buf[20];
    sprintf(buf,"%I64d", i );
    os << buf;
    return os;
  }
#endif

int main(int argc, char* argv[])
{
	char endpoint[256];
	const char* url="http://localhost:80/axis/Calculator";
	char dateTime[50];

	xsd__boolean* boolResult;
	xsd__byte* bResult;
	xsd__unsignedByte* ubResult;
	xsd__short* sResult;
	xsd__unsignedShort* usResult;
	xsd__int* iResult;
	xsd__unsignedInt* uiResult;
	xsd__long* lResult;
	xsd__unsignedLong* ulResult;
	xsd__float* fResult;
	xsd__double* dResult;
	xsd__date* dateResult;			// typedef of struct tm
	xsd__dateTime* dateTimeResult;	// typedef of struct tm
	xsd__time* timeResult;			// typedef of struct tm
	xsd__duration* durationResult;
	xsd__string strResult;
	xsd__integer* intResult;
	xsd__decimal* decResult;
	xsd__base64Binary* b64Result;
	xsd__hexBinary* hexResult;

	xsd__boolean boolInput=(xsd__boolean)1;
	xsd__byte bInput=(xsd__byte)31;
	xsd__unsignedByte ubInput=(xsd__unsignedByte)32;
	xsd__short sInput=(xsd__short)7;
	xsd__unsignedShort usInput=(xsd__unsignedShort)14;
	xsd__int iInput=(xsd__int)21;
	xsd__unsignedInt uiInput=(xsd__unsignedInt)28;
	xsd__long lInput=(xsd__long)35;
	xsd__unsignedLong ulInput=(xsd__unsignedLong)42;
	xsd__float fInput=(xsd__float)35.3535888888;
	xsd__double dInput=(xsd__double)70.7175888888;
	xsd__date dateInput;			// typedef of struct tm
	xsd__dateTime dateTimeInput;	// typedef of struct tm
	xsd__time timeInput;			// typedef of struct tm
	xsd__duration durationInput = (xsd__duration) 123456789;
 	xsd__string strInput=(xsd__string)"never odd or even";
	xsd__integer intInput=(xsd__integer)919191919;
	xsd__decimal decInput=(xsd__decimal)929292929.5555555555555;
	xsd__base64Binary b64Input;
	xsd__hexBinary hexInput;

	xsd__base64Binary b64Test;
	xsd__hexBinary hexTest;

	//xsd__unsignedByte* testUB = (xsd__unsignedByte*)"never odd or even";
	xsd__unsignedByte* testUB = (xsd__unsignedByte*)"<test><xml>some dod&y string</xml></test>";

	b64Test.__ptr=testUB;
	b64Test.__size=41;
	hexTest.__ptr=testUB;
	hexTest.__size=41;

    time_t timeToTest;
    timeToTest = 1100246323;
    xsd__date *temp = gmtime(&timeToTest);
    xsd__date testDate;
    memcpy(&testDate, temp, sizeof(xsd__date));

	if(argc>1)
		url = argv[1];

	try
	{
		sprintf(endpoint, "%s", url);
		XSDElementNil* ws = new XSDElementNil(endpoint);

		boolResult = ws->setGetDataBoolean(&boolInput);
		cout << "bool=" << *boolResult << endl;

		bResult = ws->setGetDataByte(&bInput);
		printf("byte=%d\n", *bResult);

		ubResult = ws->setGetDataUnsignedByte(&ubInput);
		printf("unsigned byte=%d\n", *ubResult);

		sResult = ws->setGetDataShort(&sInput);
		printf("short=%d\n", *sResult);

		usResult = ws->setGetDataUnsignedShort(&usInput);
		printf("unsigned short=%d\n", *usResult);

		iResult = ws->setGetDataInt(&iInput);
		printf("int=%d\n", *iResult);

		uiResult = ws->setGetDataUnsignedInt(&uiInput);
		printf("unsigned int=%d\n", *uiResult);fflush(stdout);

		lResult = ws->setGetDataLong(&lInput);
		cout << "long=" << *lResult << endl;
		ulResult = ws->setGetDataUnsignedLong(&ulInput);
		printf("unsigned long=%d\n", *ulResult);

		fResult = ws->setGetDataFloat(&fInput);
		printf("float=%.5f\n", *fResult); fflush(stdout);

		dResult = ws->setGetDataDouble(&dInput);
		printf("double=%.5f\n", *dResult); fflush(stdout);

        durationResult = ws->setGetDataDurationType(&durationInput);
        cout << "duration=" << *durationResult << endl;

		dateResult = ws->setGetDateType(&testDate);
		strftime(dateTime, 50, "%a %b %d %Y", dateResult);
		cout << "date=" << dateTime << endl;

		dateTimeResult = ws->setGetDateTimeType(&testDate);
		strftime(dateTime, 50, "%a %b %d %H:%M:%S %Y", dateTimeResult);
		cout << "dateTime=" << dateTime << endl;

//  This is being commented out due to issue on some servers
//  An additional testcase XSDTimeNil has been created to isolate this test.
//		timeResult = ws->setGetTimeType(&testDate);
//		strftime(dateTime, 50, "%H:%M:%S", timeResult);
//		cout << "time=" << dateTime << endl;

		strResult = ws->setGetDataString("never odd or even");
		cout << "string=" << strResult << endl;
		strResult = ws->setGetDataString("m");
		cout << "small string=" << strResult << endl;

		intResult = ws->setGetIntegerType(&intInput);
		cout << "integer=" << *intResult << endl;

		decResult = ws->setGetDecimalType(&decInput);
        printf("decimal=%.5f\n", *decResult); fflush(stdout);

		b64Result = ws->setGetBase64BinaryType(&b64Test);
		cout << "base64Binary size=" << b64Result->__size << endl;
		if( b64Result->__size > 0)
		{
			cout << "base64Binary data=" << b64Result->__ptr << endl;
		}

		hexResult = ws->setGetHexBinary(&hexTest);
		cout << "hexBinary size=" << hexResult->__size << endl;
		if( hexResult->__size > 0)
		{
			cout << "hexBinary data=" << hexResult->__ptr << endl;
		}
		delete ws;
	}
	catch(AxisException& e)
	{
	    cout << "Exception : " << e.what() << endl;
	}
	catch(exception& e)
	{
	    cout << "Unknown exception has occured" << endl;
	}
	catch(...)
	{
	    cout << "Unknown exception has occured" << endl;
	}
	cout<< "---------------------- TEST COMPLETE -----------------------------"<< endl;
	
	return 0;
}

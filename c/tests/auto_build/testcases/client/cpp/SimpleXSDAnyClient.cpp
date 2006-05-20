// Copyright 2003-2004 The Apache Software Foundation.
// (c) Copyright IBM Corp. 2004, 2005 All Rights Reserved
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//        http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
/* NOTE NOTE NOTE NOTE NOTE NOTE NOTE NOTE NOTE NOTE NOTE NOTE NOTE   */
/* ----------------------------------------------------------------   */
/* CHANGES TO THIS FILE MAY ALSO REQUIRE CHANGES TO THE               */
/* C-EQUIVALENT FILE. PLEASE ENSURE THAT IT IS DONE.                  */
/* ----------------------------------------------------------------   */
/* NOTE NOTE NOTE NOTE NOTE NOTE NOTE NOTE NOTE NOTE NOTE NOTE NOTE   */
/* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */

#include <axis/AxisException.hpp>
#include "XSD_byte.hpp" 

#include <stdlib.h> // For malloc(), calloc(), strdup() and free()
#include <iostream>
#include <fstream>

#define WSDL_DEFAULT_ENDPOINT "http://localhost:9080/ComplexTypeAll/services/Service"

// If we re-direct cout it will be to this ofstream
ofstream output_file;

// Prototype
bool parse_args_for_endpoint(int *argc, char *argv[], char **endpoint);
void shift_args(int i, int *argc, char *argv[]);
void setLogOptions(const char *output_filename);

int main(int argc, char* argv[])
{
    XSD_byte *ws;

    char *endpoint = WSDL_DEFAULT_ENDPOINT;
    bool endpoint_set = false;
    int returnValue = 1; // Assume Failure

    endpoint_set = parse_args_for_endpoint(&argc, argv, &endpoint);

    bool bSuccess = false;
    int     iRetryIterationCount = 3;

    do
    {
        try {
            if(endpoint_set) {
                ws = new XSD_byte(endpoint, APTHTTP1_1);
                free(endpoint);
                endpoint_set = false;
            } else
                ws = new XSD_byte();

            SimpleComplexType1* input = new SimpleComplexType1();

            AnyType* any1 = new AnyType();

            AnyType *pAny = new AnyType();
            pAny->_size = 1;
            pAny->_array = new char*[1];
            pAny->_array[0]=strdup("<mybook>WSCC</mybook>");

            input->setany1(pAny);
            input->setfield2("WebServices");
            input->setfield3(123);

            SimpleComplexType1* result = NULL;
            result = ws->asComplexType(input);

            if( result == NULL )
                cout << "result object is NULL" << endl;

            AnyType* pAnyReturn = result->getany1();
            xsd__string f2 = result->getfield2();
            xsd__int f3 = result->getfield3();

            char * p = strstr( pAnyReturn->_array[0], "<mybook");

            if( p && strstr( p, ">WSCC</mybook>"))
            {
                cout << "Result field1 is = <mybook>WSCC</mybook>" << endl;
            }
            else
            {
                cout << "Result field1 is = " << pAnyReturn->_array[0] << endl;
            }

            cout << "Result field2 is = " << f2 << endl;
            cout << "Result field3 is = " << f3 << endl;

            bSuccess = true;

            returnValue = 0; // Success

        }
        catch(AxisException &e)
        {
            bool bSilent = false;

            if( e.getExceptionCode() == CLIENT_TRANSPORT_OPEN_CONNECTION_FAILED)
            {
                if( iRetryIterationCount > 0)
                {
                    bSilent = true;
                }
            }
            else
            {
                iRetryIterationCount = 0;
            }

            if( !bSilent)
            {
                cout << e.what() << endl;
            }
        }
        catch(...)
        {
            cout << "Unknown Exception occured." << endl;
        }

        // Samisa : clean up memory allocated for stub
        try
        {
            delete ws;
        }
        catch(exception& exception)
        {
            cout << "Exception on clean up of ws : " << exception.what()<<endl;
        }
        catch(...)
        {
            cout << "Unknown exception on clean up of ws : " << endl;
        }
        iRetryIterationCount--;
    } while( iRetryIterationCount > 0 && !bSuccess);
    if(endpoint_set)
        free(endpoint);
    cout << "---------------------- TEST COMPLETE -----------------------------"<< endl;

    return returnValue;
}

/* Spin through args list and check for -e -p and -s options.
   Option values are expected to follow the option letter as the next
   argument.
 
   These options and values are removed from the arg list.
   If both -e and -s and or -p, then -e takes priority
*/
bool parse_args_for_endpoint(int *argc, char *argv[], char **endpoint) {

    // We need at least 2 extra arg after program name
    if(*argc < 3)
        return false;

    char *server = "localhost";
    int  port = 80;
    bool ep_set = false;
    bool server_set = false;
    bool port_set = false;

    for(int i=1; i<*argc; i++) {
        if(*argv[i] == '-') {
            switch(*(argv[i]+1)) {
            case 'e':
                *endpoint = strdup(argv[i+1]);
                ep_set = true;
                shift_args(i, argc, argv);
                i--;
                break;
            case 's':
                server = strdup(argv[i+1]);
                server_set = true;
                shift_args(i, argc, argv);
                i--;
                break;
            case 'p':
                port = atoi(argv[i+1]);
                if(port >80) port_set = true;
                shift_args(i, argc, argv);
                i--;
                break;
            case 'o':
                setLogOptions(argv[i+1]);
                shift_args(i, argc, argv);
                i--;
                break;
            default:
                break;
            }
        }
    }

    // use the supplied server and/or port to build the endpoint
    if(ep_set == false && (server_set || port_set)) {
        // Set p to the location of the first '/' after the http:// (7 chars)
        // e.g. from http://localhost:80/axis/base gets /axis/base
        char *ep_context = strpbrk(&(*endpoint)[7], "/");

        // http://:/ is 9 characters + terminating NULL character so add 10.
        // Allow space for port number upto 999999 6 chars
        *endpoint = (char *)calloc(1, 10 + strlen(ep_context) + strlen(server) + 6);
        sprintf(*endpoint, "http://%s:%d/%s", server, port, ep_context+1);
        if(server_set) free(server);
        ep_set = true;
    }

    return ep_set;
}

void shift_args(int i, int *argc, char *argv[]) {
    for(int j=i, k=i+2; j<*(argc)-2; j++, k++)
        argv[j]=argv[k];
    *argc-=2;
}
void setLogOptions(const char *output_filename) {
    output_file.open(output_filename, ios::out);
    if(output_file.is_open()){
        cout.rdbuf( output_file.rdbuf() );
    }
}


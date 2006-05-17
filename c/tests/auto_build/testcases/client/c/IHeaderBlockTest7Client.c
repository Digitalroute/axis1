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


/* Tests addChild() API in IHeaderBlock
  Author @James Jose  
*/


#include <stdlib.h>
#include <stdio.h>
#include <time.h>

#include "CommonClientTestCode.h"
#include "Calculator.h"



int main(int argc, char* argv[])
{
    AXISCHANDLE ws;
        
    char endpoint[256];
    const char* url="http://localhost:80/axis/Calculator";
    int i1=2, i2=3;
    int iResult;
    
    AxiscChar *uri="http://ws.apache.org/";   
    AXISCHANDLE phb; 
    AXISCHANDLE Bnode1, Bnode2;
    
    axiscAxisRegisterExceptionHandler(exceptionHandler);

    if(argc > 1)
        url = argv[1];
    sprintf(endpoint, "%s", url);
    ws = get_Calculator_stub(endpoint);

    phb = axiscStubCreateSOAPHeaderBlock(ws, "TestHeader","http://ws.apache.org/", NULL);
    Bnode1=axiscHeaderBlockCreateChildBasicNode(phb, CHARACTER_NODE,NULL,NULL,NULL,"APACHE ");
    axiscHeaderBlockAddChild(phb, Bnode1);
    Bnode2=axiscHeaderBlockCreateChildBasicNode(phb, CHARACTER_NODE,NULL,NULL,NULL,"is cool");
    axiscHeaderBlockAddChild(phb, Bnode2);
    iResult=add(ws, i1, i2);
    printf("\n%d\n", iResult);

    destroy_Calculator_stub(ws);
                
    printf( "---------------------- TEST COMPLETE -----------------------------\n");
    return 0;
}

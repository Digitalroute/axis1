/*
 * This file was auto-generated by the Axis C++ Web Service Generator (WSDL2Ws)
 * This file contains functions to manipulate complex type SOAPStructStruct
 */

#include <malloc.h>
#include "SOAPStructStruct.h"
#include <axis/server/AxisWrapperAPI.h>

extern int Axis_DeSerialize_SOAPStruct(SOAPStruct* param, IWrapperSoapDeSerializer* pDZ);
extern void* Axis_Create_SOAPStruct(SOAPStruct* pObj, bool bArray = false, int nSize=0);
extern void Axis_Delete_SOAPStruct(SOAPStruct* param, bool bArray = false, int nSize=0);
extern int Axis_Serialize_SOAPStruct(SOAPStruct* param, IWrapperSoapSerializer* pSZ, bool bArray = false);
extern int Axis_GetSize_SOAPStruct();

/*
 * This static method serialize a SOAPStructStruct type of object
 */
int Axis_Serialize_SOAPStructStruct(SOAPStructStruct* param, IWrapperSoapSerializer* pSZ, bool bArray = false)
{
	if (bArray)
	{
		pSZ->serialize("<", Axis_TypeName_SOAPStructStruct, ">", NULL);
	}
	else
	{
		const AxisChar* sPrefix = pSZ->getNamespacePrefix(Axis_URI_SOAPStructStruct);
		pSZ->serialize("<", Axis_TypeName_SOAPStructStruct, " xsi:type=\"", sPrefix, ":",
			Axis_TypeName_SOAPStructStruct, "\" xmlns:", sPrefix, "=\"",
			Axis_URI_SOAPStructStruct, "\">", NULL);
	}

	pSZ->serializeAsElement("SOAPStructStruct_varString", (void*)&(param->SOAPStructStruct_varString), XSD_STRING);
	pSZ->serializeAsElement("SOAPStructStruct_varInt", (void*)&(param->SOAPStructStruct_varInt), XSD_INT);
	pSZ->serializeAsElement("SOAPStructStruct_varFloat", (void*)&(param->SOAPStructStruct_varFloat), XSD_FLOAT);
	Axis_Serialize_SOAPStruct(param->SOAPStructStruct_varStruct, pSZ);

	pSZ->serialize("</", Axis_TypeName_SOAPStructStruct, ">", NULL);
	return AXIS_SUCCESS;
}

/*
 * This static method deserialize a SOAPStructStruct type of object
 */
int Axis_DeSerialize_SOAPStructStruct(SOAPStructStruct* param, IWrapperSoapDeSerializer* pIWSDZ)
{
	param->SOAPStructStruct_varString = pIWSDZ->getElementAsString("SOAPStructStruct_varString",0);
	param->SOAPStructStruct_varInt = pIWSDZ->getElementAsInt("SOAPStructStruct_varInt",0);
	param->SOAPStructStruct_varFloat = pIWSDZ->getElementAsFloat("SOAPStructStruct_varFloat",0);
	param->SOAPStructStruct_varStruct = (SOAPStruct*)pIWSDZ->getCmplxObject((void*)Axis_DeSerialize_SOAPStruct
		, (void*)Axis_Create_SOAPStruct, (void*)Axis_Delete_SOAPStruct
		, "SOAPStruct", Axis_URI_SOAPStruct);
	return pIWSDZ->getStatus();
}
void* Axis_Create_SOAPStructStruct(SOAPStructStruct* pObj, bool bArray = false, int nSize=0)
{
	if (bArray && (nSize > 0))
	{
		if (pObj)
		{
			SOAPStructStruct* pNew = new SOAPStructStruct[nSize];
			memcpy(pNew, pObj, sizeof(SOAPStructStruct)*nSize/2);
			memset(pObj, 0, sizeof(SOAPStructStruct)*nSize/2);
			delete [] pObj;
			return pNew;
		}
		else
		{
			return new SOAPStructStruct[nSize];
		}
	}
	else
		return new SOAPStructStruct;
}

/*
 * This static method delete a SOAPStructStruct type of object
 */
void Axis_Delete_SOAPStructStruct(SOAPStructStruct* param, bool bArray = false, int nSize=0)
{
	if (bArray)
	{
		delete [] param;
	}
	else
	{
		delete param;
	}
}
/*
 * This static method gives the size of SOAPStructStruct type of object
 */
int Axis_GetSize_SOAPStructStruct()
{
	return sizeof(SOAPStructStruct);
}

SOAPStructStruct::SOAPStructStruct()
{
	/*do not allocate memory to any pointer members here
	 because deserializer will allocate memory anyway. */
	SOAPStructStruct_varStruct=0;
}

SOAPStructStruct::~SOAPStructStruct()
{
	/*delete any pointer and array members here*/
	delete SOAPStructStruct_varStruct;
}

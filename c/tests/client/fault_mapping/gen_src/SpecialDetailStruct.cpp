/*
 * This file was auto-generated by the Axis C++ Web Service Generator (WSDL2Ws)
 * This file contains functions to manipulate complex type SpecialDetailStruct
 */

#include <malloc.h>
#include "SpecialDetailStruct.h"
#include <axis/AxisWrapperAPI.hpp>

/*
 * This static method serialize a SpecialDetailStruct type of object
 */
int Axis_Serialize_SpecialDetailStruct(SpecialDetailStruct* param, IWrapperSoapSerializer* pSZ, bool bArray = false)
{
	if (bArray)
	{
		pSZ->serialize("<", Axis_TypeName_SpecialDetailStruct, ">", NULL);
	}
	else
	{
		const AxisChar* sPrefix = pSZ->getNamespacePrefix(Axis_URI_SpecialDetailStruct);
		pSZ->serialize("<", Axis_TypeName_SpecialDetailStruct, " xsi:type=\"", sPrefix, ":",
			Axis_TypeName_SpecialDetailStruct, "\" xmlns:", sPrefix, "=\"",
			Axis_URI_SpecialDetailStruct, "\">", NULL);
	}

	pSZ->serializeAsElement("varString", (void*)&(param->varString), XSD_STRING);

	pSZ->serialize("</", Axis_TypeName_SpecialDetailStruct, ">", NULL);
	return AXIS_SUCCESS;
}

/*
 * This static method deserialize a SpecialDetailStruct type of object
 */
int Axis_DeSerialize_SpecialDetailStruct(SpecialDetailStruct* param, IWrapperSoapDeSerializer* pIWSDZ)
{
	param->varString = pIWSDZ->getElementAsString("varString",0);
	return pIWSDZ->getStatus();
}
void* Axis_Create_SpecialDetailStruct(SpecialDetailStruct* pObj, bool bArray = false, int nSize=0)
{
	if (bArray && (nSize > 0))
	{
		if (pObj)
		{
			SpecialDetailStruct* pNew = new SpecialDetailStruct[nSize];
			memcpy(pNew, pObj, sizeof(SpecialDetailStruct)*nSize/2);
			memset(pObj, 0, sizeof(SpecialDetailStruct)*nSize/2);
			delete [] pObj;
			return pNew;
		}
		else
		{
			return new SpecialDetailStruct[nSize];
		}
	}
	else
		return new SpecialDetailStruct;
}

/*
 * This static method delete a SpecialDetailStruct type of object
 */
void Axis_Delete_SpecialDetailStruct(SpecialDetailStruct* param, bool bArray = false, int nSize=0)
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
 * This static method gives the size of SpecialDetailStruct type of object
 */
int Axis_GetSize_SpecialDetailStruct()
{
	return sizeof(SpecialDetailStruct);
}

SpecialDetailStruct::SpecialDetailStruct()
{
	/*do not allocate memory to any pointer members here
	 because deserializer will allocate memory anyway. */
}

SpecialDetailStruct::~SpecialDetailStruct()
{
	/*delete any pointer and array members here*/
}
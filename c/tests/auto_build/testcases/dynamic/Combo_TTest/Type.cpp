/*
 * Copyright 2003-2004 The Apache Software Foundation.

 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file was auto-generated by the Axis C++ Web Service Generator (WSDL2Ws)
 * This file contains functions to manipulate complex type Type
 */

#include <axis/AxisWrapperAPI.hpp>

#include "Type.hpp"
/*
 * This static method serialize a Type type of object
 */
int Axis_Serialize_Type(Type* param, IWrapperSoapSerializer* pSZ, bool bArray = false)
{
	if ( param == NULL )
	{
	 /* TODO : may need to check nillable value - Now done*/
		pSZ->serializeAsAttribute( "xsi:nil", 0, (void*)&(xsd_boolean_true), XSD_BOOLEAN);
		pSZ->serialize( ">", NULL);
		return AXIS_SUCCESS;
	}

	/* first serialize attributes if any*/
	pSZ->serialize( ">", 0);

	/* then serialize elements if any*/
	pSZ->serializeBasicArray((Axis_Array*)(&param->item), Axis_URI_Type,XSD_INT, "item");
	return AXIS_SUCCESS;
}

/*
 * This static method deserialize a Type type of object
 */
int Axis_DeSerialize_Type(Type* param, IWrapperSoapDeSerializer* pIWSDZ)
{
	Axis_Array array;

	array = pIWSDZ->getBasicArray(XSD_INT, "item",0);
	param->item.m_Array = (xsd__int**)new xsd__int*[array.m_Size];
	param->item.m_Size = array.m_Size;

	memcpy( param->item.m_Array, array.m_Array, sizeof( xsd__int) * array.m_Size);
	return pIWSDZ->getStatus();
}
void* Axis_Create_Type(Type* pObj, bool bArray = false, int nSize=0)
{
	if (bArray && (nSize > 0))
	{
		if (pObj)
		{
			Type* pNew = new Type[nSize];
			memcpy(pNew, pObj, sizeof(Type)*nSize/2);
			memset(pObj, 0, sizeof(Type)*nSize/2);
			delete [] pObj;
			return pNew;
		}
		else
		{
			return new Type[nSize];
		}
	}
	else
		return new Type;
}

/*
 * This static method delete a Type type of object
 */
void Axis_Delete_Type(Type* param, bool bArray = false, int nSize=0)
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
 * This static method gives the size of Type type of object
 */
int Axis_GetSize_Type()
{
	return sizeof(Type);
}

Type::Type()
{
	/*do not allocate memory to any pointer members here
	 because deserializer will allocate memory anyway. */
	item.m_Array = 0;
	item.m_Size = 0;
}

Type::~Type()
{
	/*delete any pointer and array members here*/
}

/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package test.wsdl.roundtrip;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
import test.wsdl.roundtrip.CallOptions;

/**
 * The BondInvestment class contains data members for all the
 * primitives, standard Java classes, and primitive wrapper
 * classes.  This class is used to test that all the data
 * members transmit correctly over the wire.
 *
 * @version   1.00  06 Feb 2002
 * @author    Brent Ulbricht
 */
public class BondInvestment implements java.io.Serializable {

    private boolean taxableInvestment;
    public byte taxIndicator; 
    public short docType;  
    public int stockBeta;
    public long yield;
    public float lastTradePrice;
    public double fiftyTwoWeekHigh;
    private String tradeExchange;
    public BigInteger portfolioType;
    public BigDecimal bondAmount;
    public Date callableDate;
    public byte[] byteArray;
    private short[] shortArray;
    private Boolean wrapperBoolean;
    private Byte wrapperByte;
    private Short wrapperShort;
    public Integer wrapperInteger;
    public Float wrapperFloat;
    private Double wrapperDouble;
    public Byte[] wrapperByteArray;
    public Short[] wrapperShortArray;
    public CallOptions[] options;

    public BondInvestment() {

    } // Constructor

    public void setTaxableInvestment(boolean taxableInvestment) {
        this.taxableInvestment = taxableInvestment;
    } // setTaxableInvestment

    public boolean getTaxableInvestment() {
        return this.taxableInvestment;
    } // getTaxableInvestment
    
    public void setTradeExchange(String tradeExchange) {
        this.tradeExchange = tradeExchange; 
    } // setTradeExchange

    public String getTradeExchange() {
        return this.tradeExchange;
    } // getTradeExchange

    public void setShortArray(short[] shortArray) {
        this.shortArray = shortArray;
    } // getShortArray

    public short[] getShortArray() {
        return this.shortArray;
    } // setShortArray

    public void setWrapperBoolean(Boolean wrapperBoolean) {
        this.wrapperBoolean = wrapperBoolean;
    } // setWrapperBoolean

    public Boolean getWrapperBoolean() {
        return this.wrapperBoolean;
    } // getWrapperBoolean
    
    public void setWrapperByte(Byte wrapperByte) {
        this.wrapperByte = wrapperByte;
    } // setWrapperByte

    public Byte getWrapperByte() {
        return this.wrapperByte;
    } // getWrapperByte
    
    public void setWrapperShort(Short wrapperShort) {
        this.wrapperShort = wrapperShort;
    } // setWrapperShort

    public Short getWrapperShort() {
        return this.wrapperShort;
    } // getWrapperShort

    public void setWrapperDouble(Double wrapperDouble) {
        this.wrapperDouble = wrapperDouble;
    } // setWrapperDouble

    public Double getWrapperDouble() {
        return this.wrapperDouble;
    } // getWrapperDouble

} // BondInvestment 

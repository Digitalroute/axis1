/*
 * Created on Apr 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.axis.saaj;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * @author Ashutosh Shahi
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NodeListImpl implements NodeList {
	
	List mNodes;
	
	public static final NodeList EMPTY_NODELIST = new NodeListImpl(Collections.EMPTY_LIST);
	
	/**
     * Constructor and Setter is intensionally made package access only.
     *  
     */
    NodeListImpl() {
        mNodes = new ArrayList();
    }
    
    NodeListImpl(List nodes) {
        this();
        mNodes.addAll(nodes);
    }

    void addNode(org.w3c.dom.Node node) {
        mNodes.add(node);
    }

    void addNodeList(org.w3c.dom.NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            mNodes.add(nodes.item(i));
        }
    }
    
    /**
     * Interface Implemented
     * 
     * @param index
     * @return
     */
    public Node item(int index) {
        if (mNodes != null && mNodes.size() > index) {
            return (Node) mNodes.get(index);
        } else {
            return null;
        }
    }

    public int getLength() {
        return mNodes.size();
    }

}
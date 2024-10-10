
/**
 * Title:        CUDOS Project<p>
 * Description:  Project led by
 * Roger Carpenter,
 * Department of Physiology,
 * University of Cambridge<p>
 * Copyright:    Copyright (c) Sanjay Manohar<p>
 * Company:      CUDOS<p>
 * @author Sanjay Manohar
 * @version 1.0
 */
package com.cudos.common;

//allows a container to receive selections from its contents

public interface SelectionRecipient {
	public void setSelected(Object o);
	public Object getSelected();
}
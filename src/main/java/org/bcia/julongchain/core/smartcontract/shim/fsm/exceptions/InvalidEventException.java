/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified java_package and other contents by Dingxuan on 2018-08-30
*/

package org.bcia.julongchain.core.smartcontract.shim.fsm.exceptions;

public class InvalidEventException extends Exception {

	public final String event;
	public final String state;

	public InvalidEventException(String event, String state) {
		super("Event '" + event + "' is innappropriate"
				+ " given the current state, " + state);
		this.event = event;
		this.state = state;
	}

}

/**
 * Copyright 2001-2004 The Apache Software Foundation.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 */
package org.apache.axis2.attachments;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:thilina@opensource.lk">Thilina Gunarathne </a>
 */
public class ByteArrayDataSource implements DataSource {

	private byte[] data;

	private String type;

	public ByteArrayDataSource(byte[] data, String type) {
		super();
		this.data = data;
		this.type = type;
	}

	public ByteArrayDataSource(byte[] data) {
		super();
		this.data = data;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContentType() {
		if (type == null)
			return "application/octet-stream";
		else
			return type;
	}

	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(data);
	}

	public String getName() {

		return "ByteArrayDataSource";
	}

	public OutputStream getOutputStream() throws IOException {
		throw new IOException("Not Supported");
	}
}

package com.yahoo.labs.samoa.core;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 Yahoo! Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * License
 */

import com.github.javacliparser.StringUtils;

/**
 * Class for storing global information about current version of SAMOA.
 * 
 * @author Albert Bifet
 * @version $Revision: 7 $
 */
public class Globals {

	public static final String workbenchTitle = "SAMOA: Scalable Advanced Massive Online Analysis Platform ";

	public static final String versionString = "0.0.1";

	public static final String copyrightNotice = "Copyright Yahoo! Inc 2013";

	public static final String webAddress = "http://github.com/yahoo/samoa";

	public static String getWorkbenchInfoString() {
		StringBuilder result = new StringBuilder();
		result.append(workbenchTitle);
		StringUtils.appendNewline(result);
		result.append("Version: ");
		result.append(versionString);
		StringUtils.appendNewline(result);
		result.append("Copyright: ");
		result.append(copyrightNotice);
		StringUtils.appendNewline(result);
		result.append("Web: ");
		result.append(webAddress);
		return result.toString();
	}
}

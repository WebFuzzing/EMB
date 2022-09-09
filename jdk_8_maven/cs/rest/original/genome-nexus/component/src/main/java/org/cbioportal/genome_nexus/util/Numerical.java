/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal Genome Nexus.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cbioportal.genome_nexus.util;

import org.apache.commons.lang.math.NumberRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static Utility methods for numerical operations.
 *
 * @author Selcuk Onur Sumer
 */
public class Numerical
{
    /**
     * Extracts positive integers from the given input string.
     *
     * @param input input string
     * @return  list of integers
     */
    public static List<Integer> extractPositiveIntegers(String input)
    {
        if (input == null)
        {
            return Collections.emptyList();
        }

        List<Integer> list = new ArrayList<>();
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(input);

        while (m.find()) {
            list.add(Integer.parseInt(m.group()));
        }

        return list;
    }

    /**
     * Checks if the given input value overlaps the start and end values.
     * Input value can be a range value too.
     *
     * This function assumes that start value is smaller than the end value.
     *
     * @param input input string (a single value or a range value)
     * @param start start value
     * @param end   end value
     * @return      true if there is an overlap between values
     */
    public static boolean overlaps(String input, Integer start, Integer end)
    {
        Integer startValue = start;
        Integer endValue = end;
        Integer minPos = null;
        Integer maxPos = null;
        boolean overlap = false;
        List<Integer> positions = extractPositiveIntegers(input);

        if (positions.size() > 0)
        {
            minPos = Collections.min(positions);
            maxPos = Collections.max(positions);
        }

        NumberRange range;

        if (startValue != null)
        {
            // if end value is not valid use start value as the end value
            if (endValue == null || endValue < startValue) {
                endValue = startValue;
            }

            range = new NumberRange(startValue, endValue);

            // check for an overlap
            if (range.containsNumber(minPos) ||
                range.containsNumber(maxPos))
            {
                overlap = true;
            }
        }

        // input can be a range value too!
        if (minPos != null && maxPos != null)
        {
            range = new NumberRange(minPos, maxPos);

            if (range.containsNumber(startValue) ||
                range.containsNumber(endValue))
            {
                overlap = true;
            }
        }

        return overlap;
    }
}

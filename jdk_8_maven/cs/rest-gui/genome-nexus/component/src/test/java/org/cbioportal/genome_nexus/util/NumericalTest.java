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

import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for Numerical utils.
 *
 * @author Selcuk Onur Sumer
 */
public class NumericalTest
{
    @Test
    public void positiveIntegerExtractor()
    {
        List<Integer> result;
        Iterator<Integer> iterator;

        // test null
        result = Numerical.extractPositiveIntegers(null);
        assertEquals(0, result.size());

        // test invalid string
        result = Numerical.extractPositiveIntegers("I have no integers!");
        assertEquals(0, result.size());

        // test the single point input
        result = Numerical.extractPositiveIntegers("V600");
        iterator = result.iterator();

        assertEquals(1, result.size());
        assertEquals((Integer)600, iterator.next());

        // test the range input
        result = Numerical.extractPositiveIntegers("666-668");
        iterator = result.iterator();

        assertEquals(2, result.size());
        assertEquals((Integer)666, iterator.next());
        assertEquals((Integer)668, iterator.next());
    }

    @Test
    public void overlaps()
    {
        assertTrue(Numerical.overlaps("667", 666, 668));
        assertTrue(Numerical.overlaps("667-700", 666, 668));
        assertTrue(Numerical.overlaps("660-667", 666, 668));
        assertTrue(Numerical.overlaps("668-680", 666, 668));
        assertTrue(Numerical.overlaps("600-666", 666, 668));

        assertFalse(Numerical.overlaps("777", 666, 668));
        assertFalse(Numerical.overlaps("700-707", 666, 668));
        assertFalse(Numerical.overlaps("600", 666, 668));
        assertFalse(Numerical.overlaps("600-606", 666, 668));

        assertTrue(Numerical.overlaps("665-669", 666, 668));
    }
}

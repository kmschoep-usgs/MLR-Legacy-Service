package gov.usgs.wma.mlrlegacy.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

public class LoggedTransactionTest {

    @Test
    public void getChangesTest() {
        Map<String, String> oldFields = new HashMap<>();
        oldFields.put("field1", "test1");
        oldFields.put("field2", "test2");
        oldFields.put("field3", null);
        oldFields.put("field4", "test4");
        Map<String, String> changedFields = new HashMap<>();
        changedFields.put("field1", "test1_mod");
        changedFields.put("field2", "test2_mod");
        changedFields.put("field3", "test3_mod");
        LoggedTransaction transaction = new LoggedTransaction();
        transaction.setOldFields(oldFields);
        transaction.setChangedFields(changedFields);

        List<LoggedTransaction.ValueChange> changes = transaction.getChanges();

        assertEquals(3, changes.size());
        LoggedTransaction.ValueChange check = transaction.getChanges().stream().filter(c -> c.column.equals("field1")).collect(Collectors.toList()).get(0);
        assertNotNull(check);
        assertEquals("test1", check.oldValue);
        assertEquals("test1_mod", check.newValue);

        check = transaction.getChanges().stream().filter(c -> c.column.equals("field2")).collect(Collectors.toList()).get(0);
        assertNotNull(check);
        assertEquals("test2", check.oldValue);
        assertEquals("test2_mod", check.newValue);

        check = transaction.getChanges().stream().filter(c -> c.column.equals("field3")).collect(Collectors.toList()).get(0);
        assertNotNull(check);
        assertEquals(null, check.oldValue);
        assertEquals("test3_mod", check.newValue);
    }

    @Test
    public void getChangesEmptyTest() {
        Map<String, String> oldFields = new HashMap<>();
        oldFields.put("field1", "test1");
        oldFields.put("field2", "test2");
        oldFields.put("field3", null);
        oldFields.put("field4", "test4");
        Map<String, String> changedFields = new HashMap<>();
        LoggedTransaction transaction = new LoggedTransaction();
        transaction.setOldFields(oldFields);
        transaction.setChangedFields(changedFields);

        assertTrue(transaction.getChanges().isEmpty());
    }

    @Test
    public void getChangesNullTest() {
        Map<String, String> oldFields = new HashMap<>();
        oldFields.put("field1", "test1");
        oldFields.put("field2", "test2");
        oldFields.put("field3", null);
        oldFields.put("field4", "test4");;
        LoggedTransaction transaction = new LoggedTransaction();
        transaction.setOldFields(oldFields);

        assertTrue(transaction.getChanges().isEmpty());
    }

    @Test
    public void getAffectedDistrictCodesTest() {
        Map<String, String> oldFields = new HashMap<>();
        oldFields.put(LoggedTransaction.DISTRICT_CODE_COLUMN, null);
        Map<String, String> changedFields = new HashMap<>();
        changedFields.put(LoggedTransaction.DISTRICT_CODE_COLUMN, "12");
        LoggedTransaction transaction = new LoggedTransaction();
        transaction.setOldFields(oldFields);
        transaction.setChangedFields(changedFields);

        List<String> codes = transaction.getAffectedDistricts();
        assertEquals(1, codes.size());
        assertTrue(codes.contains("12"));

        oldFields.put(LoggedTransaction.DISTRICT_CODE_COLUMN, "55");
        changedFields.put(LoggedTransaction.DISTRICT_CODE_COLUMN, null);
        transaction.setOldFields(oldFields);
        transaction.setChangedFields(changedFields);
        codes = transaction.getAffectedDistricts();
        assertEquals(1, codes.size());
        assertTrue(codes.contains("55"));

        changedFields.put(LoggedTransaction.DISTRICT_CODE_COLUMN, "55");
        transaction.setOldFields(oldFields);
        transaction.setChangedFields(changedFields);
        codes = transaction.getAffectedDistricts();
        assertEquals(1, codes.size());
        assertTrue(codes.contains("55"));

        changedFields.put(LoggedTransaction.DISTRICT_CODE_COLUMN, "22");
        transaction.setOldFields(oldFields);
        transaction.setChangedFields(changedFields);
        codes = transaction.getAffectedDistricts();
        assertEquals(2, codes.size());
        assertTrue(codes.containsAll(Arrays.asList("55", "22")));

        oldFields.put(LoggedTransaction.DISTRICT_CODE_COLUMN, null);
        changedFields.put(LoggedTransaction.DISTRICT_CODE_COLUMN, null);
        transaction.setOldFields(oldFields);
        transaction.setChangedFields(changedFields);
        codes = transaction.getAffectedDistricts();
        assertEquals(0, codes.size());
    }
}
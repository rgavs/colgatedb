package colgatedb.tuple;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * ColgateDB
 *
 * @author Michael Hay mhay@colgate.edu
 * <p>
 * ColgateDB was developed by Michael Hay but borrows considerably from past
 * efforts including SimpleDB (developed by Sam Madden at MIT) and its predecessor
 * Minibase (developed at U. of Wisconsin by Raghu Ramakrishnan).
 * <p>
 * The contents of this file are taken almost verbatim from the SimpleDB project.
 * We are grateful for Sam's permission to use and adapt his materials.
 */

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

  /**
   * A helper class to facilitate organizing the information of each field
   */
  public static class TDItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The type of the field
     */
    public final Type fieldType;

    /**
     * The name of the field
     */
    public final String fieldName;

    TDItem(Type t, String n) {
      this.fieldName = n;
      this.fieldType = t;
    }

    public String toString() {
      return fieldName + "(" + fieldType + ")";
    }
  }

  private static final long serialVersionUID = 1L;
  private TDItem[] tdItems;
  private boolean strings = false;
  private int size = 0;

  /**
   * Create a new TupleDesc with typeAr.length tdItems of the
   * specified types and with associated names.
   *
   * @param typeAr  array specifying the number of and types of tdItems in this
   *                TupleDesc. It must contain at least one entry.
   * @param fieldAr array specifying the names of the tdItems. Note that names may
   *                be null.
   */
  public TupleDesc(Type[] typeAr, String[] fieldAr) {
    if (typeAr.length != fieldAr.length){
      throw new RuntimeException("Input arguments are of incompatible length.");
    }
    tdItems = new TDItem[typeAr.length];
    for (int i = 0; i < tdItems.length; i++) {
      if(!this.strings) {
        if (!fieldAr[i].equals("") && !fieldAr[i].equals(null)) {
          this.tdItems[i] = new TDItem(typeAr[i], fieldAr[i]);
          this.strings = true;
        }
        else {
          this.tdItems[i] = new TDItem(typeAr[i], "");
        }
      }
    }
    if (!strings) {
      throw new NoSuchElementException("All field names are null");
    }
  }

  /**
   * Constructor. Create a new tuple desc with typeAr.length tdItems with
   * tdItems of the specified types.  Field names should be assigned as empty
   * strings.
   *
   * @param typeAr array specifying the number of and types of tdItems in this
   *               TupleDesc. It must contain at least one entry.
   */
  public TupleDesc(Type[] typeAr) {
    for(int i = 0; i < tdItems.length; i++) {
      tdItems[i] = new TDItem(typeAr[i], null);
    }
  }

  /**
   * @return the number of tdItems in this TupleDesc
   */
  public int numFields() {
    if (this.tdItems != null)
      return tdItems.length;
    else
      return 0;
  }

  /**
   * Gets the type of the ith field of this TupleDesc.
   *
   * @param i The index of the field to get the type of. It must be a valid
   *          index.
   * @return the type of the ith field
   * @throws NoSuchElementException if i is not a valid field reference.
   */
  public Type getFieldType(int i) throws NoSuchElementException {
    if (i >= this.numFields() || i < 0) {
      throw new NoSuchElementException();
    }
    return this.tdItems[i].fieldType;
  }

  /**
   * Gets the (possibly null) field name of the ith field of this TupleDesc.
   *
   * @param i index of the field name to return. It must be a valid index.
   * @return the name of the ith field
   * @throws NoSuchElementException if i is not a valid field reference.
   */
  public String getFieldName(int i) throws NoSuchElementException {
    if (i >= this.numFields() || i < 0) {
      throw new NoSuchElementException();
    }
    return this.tdItems[i].fieldName;
  }

  /**
   * Find the index of the field with a given name.
   *
   * @param name of the field.
   * @return the index of the field that is first to have the given name.
   * @throws NoSuchElementException if no field with a matching name is found.
   */
  public int fieldNameToIndex(String name) throws NoSuchElementException {
    if (!strings) {
      throw new NoSuchElementException("No tdItems are named, so " + name + " cannot be found.");
    }
    for (int i = 0; i < this.numFields(); i++) {
      if (this.getFieldName(i).equals(name)) {
        return i;
      }
    }
    throw new NoSuchElementException("No item with the given name " + name + " exists.");
  }

  /**
   * @return The size (in bytes) of tuples corresponding to this TupleDesc.
   * Note that tuples from a given TupleDesc are of a fixed size.  The size
   * depends on the field types.
   * @see colgatedb.tuple.Type#getLen()
   */
  public int getSize() {
    if (this.size != 0)
      return this.size;
    for (TDItem item : tdItems) {
      //noinspection ConstantConditions
      this.size += item.fieldType.getLen();
    }
    return this.size;
  }

  /**
   * @return An iterator which iterates over all the field TDItems
   * that are included in this TupleDesc
   */
  public Iterator<TDItem> iterator() {
    return Arrays.asList(this.tdItems).iterator();
  }

  /**
   * Compares the specified object with this TupleDesc for equality. Two
   * TupleDescs are considered equal if they are the same size and if the n-th
   * type in this TupleDesc is equal to the n-th type in td.
   *
   * @param o the Object to be compared for equality with this TupleDesc.
   * @return true if the object is equal to this TupleDesc.
   */
  public boolean equals(Object o) {
    if (((Object) null).equals(o) || !o.getClass().equals(this.getClass()) || ((TupleDesc) o).getSize() != this.getSize()) {
      return false;
    } else {
      for (int i = 0; i < this.tdItems.length; i++) {
        if (!tdItems[i].fieldType.equals(((TupleDesc) o).getFieldType(i))) {
          return false;
        }
      }
    }
    return true;
  }

  public int hashCode() {
    // If you want to use TupleDesc as keys for HashMap, implement this so
    // that equal objects have equals hashCode() results.  If you're not
    // sure yet (you may not be), then leave the UnsupportedOperationException
    // so that if this method ever gets called, it will trigger an exception.
    throw new UnsupportedOperationException("unimplemented");
  }

  /**
   * Returns a String describing this descriptor. It should be of the form
   * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])".
   *
   * @return String describing this descriptor.
   */
  public String toString() {
    String ret = "";
    for (TDItem item : this.tdItems) {
      if (!ret.equals("")) {
        ret += ", ";
      }
      ret += item.toString();
    }
    return ret;
  }

  /**
   * Merge two TupleDescs into one, with td1.numFields + td2.numFields tdItems,
   * with the first td1.numFields coming from td1 and the remaining from td2.
   *
   * @param td1 The TupleDesc with the first tdItems of the new TupleDesc
   * @param td2 The TupleDesc with the last tdItems of the TupleDesc
   * @return the new TupleDesc
   */
  public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
    int numFields = td1.numFields() + td2.numFields();
    Type[] typeAr = new Type[numFields];
    String[] fieldAr = new String[numFields];
    for (int i = 0; i < numFields; i++) {
      if (i < td1.numFields()) {
        typeAr[i] = td1.getFieldType(i);
        fieldAr[i] = td1.getFieldName(i);
      } else {
        typeAr[i] = td2.getFieldType(i - td1.numFields());
        fieldAr[i] = td2.getFieldName(i - td1.numFields());
      }
    }
    return new TupleDesc(typeAr, fieldAr);
  }
}

package diario.alimentare.coda.queue;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.*;

import java.io.*;
import java.util.*;

/**
 * Created by matteo
 */
public class QueueDatabaseHelper<T> extends SQLiteOpenHelper {

	public static class QueueDatabaseHelperObject<Q> {
		final long rowid;
		final Q object;

		public QueueDatabaseHelperObject(long rowid, Q object) {
			super();
			this.rowid = rowid;
			this.object = object;
		}

	}

	public static final String DATABASE_CODA = "ASYNC_REMOTE_QUEUE.db";
	public static final int version = 1;
	public static final String TABLE_CODA = "QUEUE";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_OBJECT = "OBJECT";

	public QueueDatabaseHelper(Context context) {
		super(context, DATABASE_CODA, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(getClass().getName(), "CREAZIONE TABELLE");
		String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_CODA + "( "
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ COLUMN_OBJECT + " BLOB not null)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// nothing to do
		Log.d(getClass().getName(), "UPDATE TABELLE");
		String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_CODA + "( "
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ COLUMN_OBJECT + " BLOB not null)";
		db.execSQL(sql);
	}

	/**
	 * aggiunge un oggetto alla coda restituendo il suo row id
	 *
	 * @param o
	 * @return
	 */
	public synchronized void enqueue(T o) {

		Log.d(getClass().getName(), "AGGIUNGE OGGETTO AL DB -- START");
		SQLiteDatabase db = getWritableDatabase();
		try {
			final byte[] serializ = _serialize(o);
			String sql = "INSERT INTO " + TABLE_CODA + " (" + COLUMN_OBJECT
					+ ") VALUES(?)";
			SQLiteStatement insertStmt = db.compileStatement(sql);
			insertStmt.clearBindings();
			insertStmt.bindBlob(1, serializ);
			long rowId = insertStmt.executeInsert();
			// return rowId;
		} finally {
			db.close();
		}
		
		Log.d(getClass().getName(), "AGGIUNGE OGGETTO AL DB -- END");
	}

	private byte[] _serialize(T o) {
		ByteArrayOutputStream out1 = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					new BufferedOutputStream(out1));
			out.writeObject(o);
			out.close();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		return out1.toByteArray();
	}

	private T _deserialize(byte[] buffer) {
		try {
			ObjectInput out = new ObjectInputStream(new BufferedInputStream(
					new ByteArrayInputStream(buffer)));
			Object o = out.readObject();
			out.close();
			return (T) o;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

	}

	/**
	 * ritorna il row id del prossimo oggetto da lavorare oppure -1 se non
	 * presente
	 *
	 * @return
	 */
	public synchronized QueueDatabaseHelperObject<T> getNextObject() {
		SQLiteDatabase db = getReadableDatabase();
		Log.d(getClass().getName(), "SELECT OGGETTO -- START");
		try {
			
			long rowid = -1;

			// controll se ci sono elementi in tabella - se no ritorna null
			Cursor cursor = db.rawQuery("select count(*) from " + TABLE_CODA,
					null);
			try {
				if (cursor.moveToFirst()) {
					rowid = cursor.getLong(0);
					if (rowid == 0)
						return null;
					cursor.close();
				} else {
					cursor.close();
					return null;// no element
				}
			} finally {
				cursor.close();
			}

			// prende min rowid
			cursor = db.rawQuery("select min(rowid) from " + TABLE_CODA, null);
			try {
				if (cursor.moveToFirst()) {
					rowid = cursor.getLong(0);

				} else {

					return null;
				}
			} finally {
				cursor.close();
			}

			cursor = db.rawQuery("select " + COLUMN_OBJECT + " from "
					+ TABLE_CODA + " where rowid=" + rowid, null);
			try {
				if (cursor.moveToFirst()) {
					byte[] blob = cursor.getBlob(0);
					T ris = _deserialize(blob);
					return new QueueDatabaseHelperObject<T>(rowid,ris);
				} else {
					return null;// no element
				}
			} finally {
				cursor.close();
			}

		} finally {
			db.close();
			Log.d(getClass().getName(), "SELECT OGGETTO -- END");
		}
	}


	/**
	 * ritorna l'oggetto associato all'id
	 *
	 * @return
	 */
	public synchronized List<T> getAllObject() {
		SQLiteDatabase db = getReadableDatabase();
		// db.open();
		Log.d(getClass().getName(), "ELENCO OGGETTI DB -- START");
		List<T> ris = new ArrayList<T>();
		try {
			Cursor cursor = db.rawQuery("select " + COLUMN_OBJECT + " from "
					+ TABLE_CODA + " order by rowid", null);
			if (cursor.moveToFirst()) {

				do {

					byte[] blob = cursor.getBlob(0);
					ris.add(_deserialize(blob));
				} while (cursor.moveToNext());
			} else {
				
				return ris;// no element
			}
		} finally {			
			db.close();
			
		}
		Log.d(getClass().getName(), "ELENCO OGGETTI DB -- END n.elem: "+ris.size());
		return ris;
	}

	/**
	 * rimuove l'oggetto associato all'id
	 *
	 * @return
	 */
	public synchronized void removeObject(long rowid) {
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.execSQL("delete from " + TABLE_CODA + " where rowid=" + rowid);
		} finally {
			db.close();
		}
	}

}

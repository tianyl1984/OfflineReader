package com.tianyl.android.offlinereader.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tianyl.android.offlinereader.common.FileUtil;
import com.tianyl.android.offlinereader.model.Article;

public class ArticleDBUtil extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = FileUtil.getBathPath() + "/offlinereader.db";

	public ArticleDBUtil(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table tab_article(id integer primary key autoincrement,title text,url text,status text,pathId text);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		String sql = "drop table if exists tab_article";
		db.execSQL(sql);
		onCreate(db);
	}

	public List<Article> selectAll() {
		Cursor cursor = this.getReadableDatabase().query("tab_article", null, null, null, null, null, "id");
		List<Article> articles = new ArrayList<Article>();
		while (cursor.moveToNext()) {
			Article article = new Article();
			article.setId(cursor.getInt(0));
			article.setTitle(cursor.getString(1));
			article.setUrl(cursor.getString(2));
			article.setStatus(cursor.getString(3));
			article.setPathId(cursor.getString(4));
			articles.add(article);
		}
		cursor.close();
		return articles;
	}

	public List<Article> selectUnEnd() {
		Cursor cursor = this.getReadableDatabase().query("tab_article", null, "status != ?", new String[] { Article.STATUS_END }, null, null, "id");
		List<Article> articles = new ArrayList<Article>();
		while (cursor.moveToNext()) {
			Article article = new Article();
			article.setId(cursor.getInt(0));
			article.setTitle(cursor.getString(1));
			article.setUrl(cursor.getString(2));
			article.setStatus(cursor.getString(3));
			article.setPathId(cursor.getString(4));
			articles.add(article);
		}
		cursor.close();
		return articles;
	}

	public void save(Article article) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("title", article.getTitle());
		cv.put("url", article.getUrl());
		cv.put("status", article.getStatus());
		cv.put("pathId", article.getPathId());
		long id = db.insert("tab_article", null, cv);
		article.setId(id);
	}

	public void update(Article article) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("title", article.getTitle());
		cv.put("url", article.getUrl());
		cv.put("status", article.getStatus());
		cv.put("pathId", article.getPathId());
		db.update("tab_article", cv, "id = ?", new String[] { article.getId() + "" });
	}

	public void updateStatusToEnd(long id) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("status", Article.STATUS_END);
		db.update("tab_article", cv, "id = ? ", new String[] { id + "" });
	}

	public Article get(long id) {
		Cursor cursor = this.getReadableDatabase().query("tab_article", null, "id = ?", new String[] { id + "" }, null, null, null);
		Article article = new Article();
		while (cursor.moveToNext()) {
			article.setId(cursor.getInt(0));
			article.setTitle(cursor.getString(1));
			article.setUrl(cursor.getString(2));
			article.setStatus(cursor.getString(3));
			article.setPathId(cursor.getString(4));
		}
		cursor.close();
		return article;
	}

	public void delete(long id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete("tab_article", "id = ?", new String[] { id + "" });
	}
}

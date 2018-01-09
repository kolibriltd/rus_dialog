package com.wearesputnik.istoria.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.wearesputnik.istoria.helpers.Books;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 08.06.17.
 */
@Table(name = "Books")
public class BookModel extends Model{
    @Column(name = "IdDbServer")
    public String IdDbServer;
    @Column(name = "Name")
    public String Name;
    @Column(name = "Author")
    public String Author;
    @Column(name = "Description")
    public String Description;
    @Column(name = "TextInfoList")
    public String TextInfoList;
    @Column(name = "IsView")
    public boolean IsView;
    @Column(name = "IsViewCount")
    public Integer IsViewCount;
    @Column(name = "IsViewTapCount")
    public Integer IsViewTapCount;
    @Column(name = "Raiting")
    public String Raiting;
    @Column(name = "PathCoverFile")
    public String PathCoverFile;
    @Column(name = "PathCoverFileStorage")
    public String PathCoverFileStorage;
    @Column(name = "TimerStopMin")
    public String TimerStopMin;
    @Column(name = "TapStooBool")
    public boolean TapStooBool;
    @Column(name = "TypeId")
    public Integer TypeId;
    @Column(name = "LastModified")
    public String LastModified;
    @Column(name = "NewIstori")
    public Integer NewIstori;
    @Column(name = "BranchJsonSave")
    public String BranchJsonSave;
    @Column(name = "BranchJsonEnd")
    public String BranchJsonEnd;
    @Column(name = "BranchJson")
    public String BranchJson;
    @Column(name = "isRaiting")
    public boolean isRaiting;


    public static void AddBook(Books item) {
        BookModel bookModel = new BookModel();
        bookModel.IdDbServer = item.id_book + "";
        bookModel.Name = item.name;
        bookModel.Author = item.author;
        bookModel.Description = item.description;
        bookModel.IsViewCount = item.isViewCount;
        bookModel.Raiting = item.raiting;
        bookModel.PathCoverFile = item.pathCoverFile;
        bookModel.TypeId = item.type_id;
        bookModel.TextInfoList = item.textInfoList;
        bookModel.LastModified = item.last_modified;
        bookModel.isRaiting = false;
        bookModel.NewIstori = NewIstoriParametr(item.last_modified);
        bookModel.save();
    }

    public static void EditBook(Books item) {
        BookModel bookModelOne = new Select().from(BookModel.class).where("IdDbServer = ?", item.id_book).executeSingle();
        bookModelOne.Name = item.name;
        bookModelOne.Author = item.author;
        bookModelOne.Description = item.description;
        bookModelOne.IsViewCount = item.isViewCount;
        bookModelOne.Raiting = item.raiting;
        bookModelOne.PathCoverFile = item.pathCoverFile;
        bookModelOne.TypeId = item.type_id;
        bookModelOne.TextInfoList = item.textInfoList;
        bookModelOne.LastModified = item.last_modified;
        if (bookModelOne.NewIstori == null) {
            bookModelOne.NewIstori = NewIstoriParametr(item.last_modified);
        }
        else {
            bookModelOne.NewIstori = NewIstoriParametr(item.last_modified);
        }

        bookModelOne.save();
    }

    public static List<Books> ListBooksIstori() {
        List<Books> booksList = new ArrayList<>();
        List<BookModel> bookModelList = new Select().from(BookModel.class).execute();
        if (bookModelList != null) {
            for (BookModel item : bookModelList) {
                Books itemAdapter = new Books();
                itemAdapter.pathCoverFileStorage = item.getId().toString();
                itemAdapter.id_book = Integer.parseInt(item.IdDbServer);
                itemAdapter.name = item.Name;
                itemAdapter.author = item.Author;
                itemAdapter.isViewCount = item.IsViewCount;
                itemAdapter.pathCoverFile = item.PathCoverFile;
                itemAdapter.pathCoverFileStorage = item.PathCoverFileStorage;
                itemAdapter.raiting = item.Raiting;
                itemAdapter.new_istori_int = NewIstoriParametr(item.LastModified);
                itemAdapter.flagGuest = false;
                booksList.add(itemAdapter);
            }
        }
        else {
            return null;
        }
        return booksList;
    }

    private static Integer NewIstoriParametr(String servLastModified) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateServer;

        try {
            dateServer = format.parse(servLastModified);
            long dateDifferent = (new Date().getTime() - dateServer.getTime()) / (24*60*60*1000);
            if (dateDifferent <= 2) {
                return 1;
            }
            else {
                return 2;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 1;
    }
}

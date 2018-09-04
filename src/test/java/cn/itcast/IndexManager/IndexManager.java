package cn.itcast.IndexManager;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class IndexManager {


    @Test
    public void testAdd() throws Exception {
//		D:\class54\lucene_index
//		1、指定把索引创建的路径
        Directory directory = FSDirectory.open(new File("G:\\IndexManager"));
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        //		2、使用IndexWriter对象创建索引
        IndexWriter  indexWriter = new IndexWriter(directory, config);


//		读取磁盘上的文件
        File fileDirectory = new File("G:\\testIndex");
        File[] files = fileDirectory.listFiles();
        for (File file : files) {
            Document doc = new Document();
//			标题  fileName
            String fileName = file.getName();
            if("spring5mvc第三天.doc".equals(fileName)) {
                TextField textField = new TextField("name", fileName, Field.Store.YES);
                textField.setBoost(10);//设置打分  默认是1
                System.out.println("有广告");
                doc.add(textField);
            }else {
                doc.add(new TextField("name", fileName, Field.Store.YES));
            }

//			文件大小：fileSize
            long fileSize = FileUtils.sizeOf(file); // 单位：字节 b
            doc.add(new TextField("size", fileSize+"", Field.Store.YES));
//			文件路径：filePath
            String filePath = file.getPath();
            doc.add(new TextField("path", filePath, Field.Store.YES));
//			文件内容：fileContent
            String fileContent = FileUtils.readFileToString(file);
            doc.add(new TextField("content", fileContent, Field.Store.YES));

            indexWriter.addDocument(doc);
        }

        indexWriter.close();
    }

    @Test
    public void deleteAll() throws Exception {
//		D:\class54\lucene_index
//		1、指定把索引创建的路径
        Directory directory = FSDirectory.open(new File("G:\\IndexManager"));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        //		2、使用IndexWriter对象创建索引
        IndexWriter  indexWriter = new IndexWriter(directory, config);
        indexWriter.deleteAll();

        indexWriter.close();
    }

    @Test
    public void testIndexManager() throws IOException {

        Directory directory = FSDirectory.open(new File("G:\\IndexManager"));

        Analyzer analyzer = new StandardAnalyzer();

        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,analyzer);

        IndexWriter indexWriter = new IndexWriter(directory,config);

        File file = new File("G:\\testIndex");
        //获取目录下的所有文件
        File[] files = file.listFiles();
        //便利得到每个文件
        for (File file1 : files) {
            Document doc = new Document();

            //获取文件名称
            String fileName = file1.getName();
            if("spring5mvc第三天.doc".equals(fileName)){
                TextField textField = new TextField("name",fileName,Field.Store.YES);
                textField.setBoost(100000);
                System.out.println("有广告");
                doc.add(textField);
            }else {
                doc.add(new TextField("name",fileName,Field.Store.YES));

            }

            //获取文件大小
            long size = FileUtils.sizeOf(file1);
            doc.add(new TextField("size",size+"",Field.Store.YES));
            //获得文件路径
            String path = file.getPath();
            doc.add(new TextField("path",path,Field.Store.YES));
            //获得文件内容
            String fileContent = FileUtils.readFileToString(file1);
            doc.add(new TextField("content",fileContent,Field.Store.YES));

            indexWriter.addDocument(doc);
        }
        indexWriter.close();
    }

    @Test
    public void testSearch() throws IOException {

        Directory directory = FSDirectory.open(new File("G:\\IndexManager"));

        IndexReader indexReader = DirectoryReader.open(directory);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Term term = new Term("name","spring");

        Query query= new TermQuery(term);

        TopDocs topDocs = indexSearcher.search(query, 100);//第二个参数;查询的最大数量

        System.out.println("查询出的总条数"+topDocs.totalHits);

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docid = scoreDoc.doc;
            Document doc = indexSearcher.doc(docid);

            System.out.println(doc.get("name"));
           /* System.out.println(doc.get("path"));
            System.out.println(doc.get("size"));*/
//            System.out.println(doc.get("content"));

        }
        indexReader.close();
    }
    @Test
    public void testAnalyzer() throws IOException {
        /*Analyzer analyzer = new StandardAnalyzer();*/
        /*Analyzer analyzer = new CJKAnalyzer();*/

        Analyzer analyzer = new SmartChineseAnalyzer();

        /*TokenStream tokenStream = analyzer.tokenStream("test", "The Spring Framework provides a comprehensive programming and configuration model.");*/
        TokenStream tokenStream = analyzer.tokenStream("test", "lucene我是中国人");
        tokenStream.reset();
        CharTermAttribute addAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        while (tokenStream.incrementToken()){
            System.out.println(addAttribute);
        }
    }



    @Test
    public void testSearch2() throws Exception {
        Directory directory = FSDirectory.open(new File("D:\\class54\\lucene_index"));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//		field:value
        Term term = new Term("name", "spring");
        Query query = new TermQuery(term );
        TopDocs topDocs = indexSearcher.search(query, 100); //第二个参数：查询的最大数量

//		topDocs.totalHits
        System.out.println("查询出的总条数"+topDocs.totalHits);

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document doc = indexSearcher.doc(docId);
            System.out.println(doc.get("name"));
//			System.out.println(doc.get("size"));
//			System.out.println(doc.get("path"));
//			System.out.println(doc.get("content"));
        }

        indexReader.close();
//		spring
//		spring 简 介 简介 txt
//		spring txt   权重大：占比高
//		spring README txt



    }
}

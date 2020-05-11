import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queries.*;


import java.io.*;
import java.util.*;


public class Lucene<SimpleHTMLFormatter, Fragmenter, QueryScorer extends Scorer, WeightedSpanTerm, i> {
    //private int count_r = 0, count_e = 0;
    //int count_s = 0;
    //int error_e = 0, error_r = 0;

    private long i = 0;
    public void testIndex() throws Exception {
        String sourceDir = "C:\\Users\\User\\Desktop\\index\\testin";
        String indexDir = "C:\\Users\\User\\Desktop\\index\\out";
        // 创建一个indexwriter对象
        Directory directory = FSDirectory.open(new File(indexDir).toPath());
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        //config.setOpenMode(IndexWriterConfig.OpenMode.CREATE); //每次都重新创建
        indexWriter.deleteAll();
        indexWriter.commit();//实时进行更改

        //索引的创建
        File sourceFiles = new File(sourceDir);
        int counter = traverse(sourceFiles, indexWriter);;

        // 关闭IndexWriter对象
        indexWriter.close();
        System.out.println(sourceDir + "目录下共有" + counter + "个文件");
    }

   private int count = 0;

    private int traverse(File sourceFiles, IndexWriter indexWriter) throws Exception {
        FilePre filePre = new FilePre();
        if(sourceFiles.exists()){
            File[] files = sourceFiles.listFiles();
            assert files != null;
            for(File file : files) {
                count++;
                if(file.isFile()){
                    System.out.println(i++);
                    Document document = new Document();
                    // 文件名称
                    String file_name = file.getName();
                    Field fileNameField = new StringField("fileName", file_name, Field.Store.YES);
                    document.add(fileNameField);

                    // 文件路径
                    String file_path = file.getPath();
                    Field filePathField = new StoredField("filePath", file_path);
                    document.add(filePathField);
                    System.out.println(file_path);

                    // 内容
                    String contents = getContent(file);
                    Field content= new StringField("content", contents, Field.Store.YES);
                    document.add(content);

                    // 使用indexWriter对象将document对象写入索引库，此过程进行索引创建。并将索引和document对象写入索引库
                    indexWriter.addDocument(document);
                }
            }
            return count;
        }
        else{
            return 0;
        }
    }

    public HashMap testSearch(int indexName, String toSearch, int mode, boolean print) throws Exception {
        Analyzer analyzer = new StandardAnalyzer();
        //System.out.println("email: "+count_e+" receiver: "+count_r +" sender: "+count_s);
        //System.out.println("error_email: "+error_e+" error_receiver: "+error_r);
        String indexDir = "C:\\Users\\User\\Desktop\\index\\out";
        Directory directory = FSDirectory.open(new File(indexDir).toPath());// 磁盘硬盘库
        IndexReader indexReader = DirectoryReader.open(directory);// 流
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        //获取查询的索引类别
        String index;
        switch(indexName){
            case 1: index = "fileName";break;
            case 2: index = "content";break;
            default: index = "filePath";break;
        }
        Term term = new Term(index, toSearch);

        //获取查询的方式并查询
        Query query;
        switch(mode){
            case 1:{
                query = new TermQuery(term);
                break;
            }
            case 2:{
                query = new FuzzyQuery(term);
                break;
            }
            default:{
                QueryParser q = new QueryParser(index, analyzer);
                query = q.parse(toSearch);
                break;
            }
        }

        // 执行查询
        int totalQuery = indexSearcher.count(query);
        System.out.println("共命中" + totalQuery + "条记录");
        TopDocs topDocs = indexSearcher.search(query, totalQuery);
        // 返回查询结果,遍历查询结果并输出

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;// 文档id
        FileWriter fw = new FileWriter("result.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        if(print){
            bw.write("查询:"+index+"索引下的"+toSearch+"内容\n");
            bw.append("共命中").append(String.valueOf(indexSearcher.count(query))).append("条记录\n");
            bw.append("打印").append(String.valueOf(scoreDocs.length)).append("条记录");
        }
        int i = 1;
        HashMap<String, Double> scoresForReviews = new HashMap<>();
        for (ScoreDoc scoreDoc : scoreDocs) {
            int doc = scoreDoc.doc;
            Document document = indexSearcher.doc(doc);
            String fileName = document.get("fileName");
            String filePath = document.get("filePath");
            String word = document.get("content");
            double score = getScore(toSearch);
            if(scoresForReviews.containsKey(fileName)){
                score = score + scoresForReviews.get(fileName);
                scoresForReviews.put(fileName,score);
            }
            else{
                scoresForReviews.put(fileName,score);
            }
            if(i<=10){
                System.out.println("=================["+i+"]=================");
                System.out.println("File ID: "+scoreDoc.doc+" FileScore: "+scoreDoc.score );

                // 文件名称
                System.out.println("fileName: " + fileName);
                // 文件路径
                System.out.println("filePath: "+filePath);
                // 检索到的单词
                System.out.println(word);
                System.out.println("-----------------");
            }

            if(print){
                bw.append("=================[").append(String.valueOf(i)).append("]=================\n");
                bw.append("File ID: ").append(String.valueOf(scoreDoc.doc)).append(" FileScore: ").append(String.valueOf(scoreDoc.score) + '\n');
                bw.append("fileName: ").append(fileName+ '\n');
                bw.append("filePath: ").append(filePath+ '\n');
                bw.append("Content: ").append(word+ '\n');
                bw.append("-----------------\n");
            }
            i++;
        }

        bw.close();
        fw.close();
        indexReader.close();
        return scoresForReviews;
    }


    private String getContent(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String contents=br.readLine();
        br.close();
        fr.close();
        return contents;
    }

    private double getScore(String toSearch){
        HashMap<String, Double> hairdryerReviews1 = new HashMap<>();
        hairdryerReviews1.put("no power",0.116);
        hairdryerReviews1.put("no air",0.09);
        hairdryerReviews1.put("don't waste",0.086);
        hairdryerReviews1.put("waste",0.081);
        hairdryerReviews1.put("not buy",0.068);
        hairdryerReviews1.put("dead",0.067);
        hairdryerReviews1.put("exploded",0.065);
        hairdryerReviews1.put("humming",0.064);
        hairdryerReviews1.put("refurbished",0.057);
        hairdryerReviews1.put("unusable",0.057);
        hairdryerReviews1.put("refund",0.053);
        hairdryerReviews1.put("junk",0.051);
        hairdryerReviews1.put("needless",0.049);
        hairdryerReviews1.put("garbage",0.049);
        hairdryerReviews1.put("defective",0.047);


        if(hairdryerReviews1.containsKey(toSearch)){
            return hairdryerReviews1.get(toSearch);
        }
        return 0;
    }
}

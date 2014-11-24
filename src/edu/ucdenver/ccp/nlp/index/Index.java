package edu.ucdenver.ccp.nlp.index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;








//import BANNER
import banner.eval.BANNER;
import banner.util.SentenceBreaker;

public class Index{
  
  public static void main(String[] args) throws IOException, ParseException, ConfigurationException{
     
	//treat as a large file - use some buffering
	ArrayList<ArrayList<String>> articles = Index.readLargerTextFile(FILE_NAME);

    // 0. Specify the analyzer for tokenizing text.
    //    The same analyzer should be used for indexing and searching
    //StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
	EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_40);
	
    // 1. create the index
    //Directory index = new RAMDirectory();
    Directory index = FSDirectory.open(new File("./index"));
    
    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
    config.setOpenMode(OpenMode.CREATE); //index everything from the scratch.
    IndexWriter w = new IndexWriter(index, config);     
    
    int counter = 0; //this does nothing. Used as senetenceId since it's required by the BANNER.tag() method.
    
	for( ArrayList<String> al: articles) {	
		
		SentenceBreaker sb = new SentenceBreaker();		
		//extract gene names in the abstracts and title of articles
		sb.setText(al.get(1) + " " + al.get(2));
		ArrayList<String> mentions = new ArrayList<String>();
		for (String sentence : sb.getSentences()){
			 ArrayList<String> mention = BANNER.tag(new XMLConfiguration(args[0]), sentence, Integer.toString(counter));
			 mentions.addAll(mention); //add mentions recognized in a sentence a list 
			counter++;
		}
		
		//convert list of mentions to a string that way is easy to index.
		String mentionToString = "";

		for (String s : mentions) {
		    mentionToString += s;
			mentionToString += " ";
		}
		 
		
				
        if (al.size() == 3){
            addDoc(w,al.get(0), al.get(1), al.get(2), mentionToString);	 
        } else {
            addDoc(w,al.get(0), al.get(1), mentionToString);
             
        	}
        }
    w.close();
 
	}

	final static String FILE_NAME = "./resources/t.txt";
	//final static String OUTPUT_FILE_NAME = "./output.txt";
	final static Charset ENCODING = StandardCharsets.UTF_8;

    private static void addDoc(IndexWriter w, String pmid, String title, String mentions) throws IOException {
        Document doc = new Document();
        
        // use a string field for pmid because we don't want it tokenized
        doc.add(new StringField("pmid", pmid, Field.Store.YES));
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("mentions", mentions, Field.Store.YES));

        w.addDocument(doc);
        }
                              

    private static void addDoc(IndexWriter w, String pmid, String title, String abs, String mentions) throws IOException {
        Document doc = new Document();
        
        // use a string field for pmid because we don't want it tokenized
        doc.add(new StringField("pmid", pmid, Field.Store.YES));
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("abs", abs, Field.Store.YES));
        doc.add(new TextField("mentions", mentions, Field.Store.YES));
        
        w.addDocument(doc); // there is a better way to do this. Update existing docs and add new docs.
        }

	public static ArrayList<ArrayList<String>> readLargerTextFile(String aFileName) throws IOException {
			Path path = Paths.get(aFileName);
			String pmid = null;
			String title = null;
			String abs = null;
		
			ArrayList<ArrayList<String>> listOfArticles = new ArrayList<ArrayList<String>>();	
			ArrayList<String> lst = new ArrayList<String>();

			try (Scanner scanner =  new Scanner(path, ENCODING.name())){
					while (scanner.hasNextLine()){
							//process each line in some way
							String line = scanner.nextLine();
							if (line.startsWith("<PMID>")) {
								pmid = line.split("<PMID>")[1].trim();
								title = null;
								abs = null;
							}
							else if (line.startsWith("<TITLE>")){
								title = line.split("<TITLE>")[1].trim();
							}
							else if (line.startsWith("<ABSTRACT>")){
								abs = line.split("<ABSTRACT>")[1].trim();
								//Change article into list of pmid, title and abstract format.
								lst.add(pmid);
								lst.add(title);
								lst.add(abs);
								//add article into a list
								listOfArticles.add(new ArrayList<String>(lst)); //copy by value. 
								lst.clear();
							}
						//log(scanner.nextLine());
						
					}      
			}
		return listOfArticles;
	}



	public static void writeLargerTextFile(String aFileName, List<String> aLines) throws IOException {
	Path path = Paths.get(aFileName);
	try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)){
			for(String line : aLines){
					writer.write(line);
					writer.newLine();
					}
			}
	}

	private static void log(Object aMsg){
			System.out.println(String.valueOf(aMsg));
			}


	}//end of class
    
     


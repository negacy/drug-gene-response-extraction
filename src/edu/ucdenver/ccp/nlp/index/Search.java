package edu.ucdenver.ccp.nlp.index;

//package edu.ucdenver.ccp.nlp;


/*
004     * Licensed to the Apache Software Foundation (ASF) under one or more
005     * contributor license agreements.  See the NOTICE file distributed with
006     * this work for additional information regarding copyright ownership.
007     * The ASF licenses this file to You under the Apache License, Version 2.0
008     * (the "License"); you may not use this file except in compliance with
009     * the License.  You may obtain a copy of the License at
010     *
011     *     http://www.apache.org/licenses/LICENSE-2.0
012     *
013     * Unless required by applicable law or agreed to in writing, software
014     * distributed under the License is distributed on an "AS IS" BASIS,
015     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
016     * See the License for the specific language governing permissions and
017     * limitations under the License.
018     */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/** Simple command-line based search demo. */
public class Search {
	
	private Search() {}
	
	/** Simple command-line based search demo. */
	public static void main(String[] args) throws Exception {
		 
		
		String index = "index";
		 
		String queries = null;
		 
		String queryString = null;
		int hitsPerPage = 100;
		 
		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		//Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
		EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_40);
		
		BufferedReader in = null;
		in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		//query building starts here.
		//QueryParser parser = new QueryParser(Version.LUCENE_40, "title", analyzer);
		MultiFieldQueryParser parser = new MultiFieldQueryParser( Version.LUCENE_40, new String[] {"title", "abs", "mentions"}, analyzer);
	    
	    
		while (true) {
			if (queries == null && queryString == null) {                        // prompt the user
				//c for cisplatin
				
				System.out.println("Enter query: ");
			}
			
			String line = queryString != null ? queryString : in.readLine();
			
			if (line == null || line.length() == -1) {
				break;
			}
			
			line = line.trim();
			if (line.length() == 0) {
				break;
			}
			
			//Query q = queryParser.parse(querystr);
			Query query = parser.parse(line);
			//System.out.println("Searching for: " + query.toString(field));
 
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
            searcher.search(query, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            // 4. display results
            System.out.println("Found " + hits.length + " hits.");
            
            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println((i + 1) + ". " + d.get("pmid") + "\t" + d.get("title"));
            }
             
			//doPagingSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null);
			
			if (queryString != null) {
				break;
			}
		}
		reader.close();
	}
	
	/**
	 139       * This demonstrates a typical paging search scenario, where the search engine presents 
	 140       * pages of size n to the user. The user can then go to the next page if interested in
	 141       * the next hits.
	 142       * 
	 143       * When the query is executed for the first time, then only enough results are collected
	 144       * to fill 5 result pages. If the user wants to page beyond this limit, then the query
	 145       * is executed another time and all hits are collected.
	 146       * 
	 147       */
	/**
	 * @param in
	 * @param searcher
	 * @param query
	 * @param hitsPerPage
	 * @param raw
	 * @param interactive
	 * @throws IOException
	 */
	/**
	 * @param in
	 * @param searcher
	 * @param query
	 * @param hitsPerPage
	 * @param raw
	 * @param interactive
	 * @throws IOException
	 */
	public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, 
									  int hitsPerPage, boolean raw, boolean interactive) throws IOException {
		
		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;
		
		int numTotalHits = results.totalHits;
		System.out.println(numTotalHits + " total matching documents");
		
		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);
		
		while (true) {
			if (end > hits.length) {
				System.out.println("Only results 1 - " + hits.length +" of " + numTotalHits + " total matching documents collected.");
				System.out.println("Collect more (y/n) ?");
				String line = in.readLine();
				if (line.length() == 0 || line.charAt(0) == 'n') {
					break;
				}
				
				hits = searcher.search(query, numTotalHits).scoreDocs;
			}
			
			end = Math.min(hits.length, start + hitsPerPage);
			
			for (int i = start; i < end; i++) {
				if (raw) {                              // output raw format
					System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
					continue;
				}
				
				Document doc = searcher.doc(hits[i].doc);
				//String path = doc.get("path");
				//if (path != null) {
				System.out.print((i+1));
				String title = doc.get("title");
				String pmid = doc.get("pmid");
				
				if (title != null || pmid != null) {
						System.out.println(doc.get("pmid") + " " + doc.get("title") );
					}
				//}
					else {
					System.out.println((i+1) + ". " + "No path for this document");
				}
				
			}
			
			if (!interactive || end == 0) {
				break;
			}
			
			if (numTotalHits >= end) {
				boolean quit = false;
				while (true) {
					System.out.print("Press ");
					if (start - hitsPerPage >= 0) {
						System.out.print("(p)revious page, ");  
					}
					if (start + hitsPerPage < numTotalHits) {
						System.out.print("(n)ext page, ");
					}
					System.out.println("(q)uit or enter number to jump to a page.");
					
					String line = in.readLine();
					if (line.length() == 0 || line.charAt(0)=='q') {
						quit = true;
						break;
					}
					if (line.charAt(0) == 'p') {
						start = Math.max(0, start - hitsPerPage);
						break;
					} else if (line.charAt(0) == 'n') {
						if (start + hitsPerPage < numTotalHits) {
							start+=hitsPerPage;
						}
						break;
					} else {
						int page = Integer.parseInt(line);
						if ((page - 1) * hitsPerPage < numTotalHits) {
							start = (page - 1) * hitsPerPage;
							break;
						} else {
							System.out.println("No such page");
						}
					}
				}
				if (quit) break;
				end = Math.min(numTotalHits, start + hitsPerPage);
			}
		}
	}
}

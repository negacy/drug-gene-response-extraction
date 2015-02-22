TOWARD SCREENING FALSE POSITIVES IN SYNTHETIC LETHAL SCREENS WITH TEXT MINING 

Three drugs of interest:
Cisplatin
Gemcitabine
Paclitaxel
Goal: to extract genes that are sensitive/resistant to these drugs.

299 unique genes:
Targeting BRCA1-BER deficient breast cancer by ATM or DNA-PKcs blockade either alone or in combination with cisplatin for personalized therapy.
BRCA2 inhibition enhances cisplatin-mediated alterations in tumor cell proliferation, metabolism, and metastasis.
The cytoprotective role of gemcitabine-induced autophagy associated with apoptosis inhibition in triple-negative MDA-MB-231 breast cancer cells.

Keyword based document collection
We used three drugs and their synonyms as keywords to search PubMed/MEDLINE to filter abstracts  that potentially contain drug-gene mechanisms.
An example of a query on PubMed: 
(Paclitaxel OR 7-Epipaclitaxel OR 7-Epitaxol OR 7-epi-Paclitaxel OR 7-epi-Taxol OR abi-007 OR Abraxane OR Epitaxol OR LipoPac OR Onxol OR Paxceed OR Paxene OR Taxol OR Taxol A OR Xorane)


XML to text conversion format:
<PMID>  25097183
<TITLE> Adjuvant Chemotherapy in Patients with Completely Resected Small Cell Lung Cancer: A Retrospective Analysis of 26 Consecutive Cases.
<ABSTRACT>  Several clinical studies have demonstrated the efficacy and safety of adjuvant chemotherapy in patients with completely resected small cell lung cancer for a selected limited stage. However, it is unclear whether adjuvant chemotherapy is feasible in clinical practice. The objective of this study was to analyze the efficacy and safety of adjuvant chemotherapy for small cell lung cancer patients retrospectively in clinical practice. From January 2002 to March 2012, 56 small cell lung cancer patients underwent surgery as initial therapy in our institute. Of these, 26 patients received adjuvant chemotherapy. The clinical data of patients who received adjuvant chemotherapy were retrospectively analyzed. The chemotherapy regimens were cisplatin and irinotecan in 16 patients, cisplatin and etoposide in 1 and carboplatin and etoposide in 9. Median follow-up time was 44.8 months. Nineteen (73%) patients received the full course of chemotherapy. Median recurrence-free survival was 21.4 months. Median survival time was not reached. There was no treatment-related death. Adjuvant chemotherapy may be generally safe and efficacious in selected small cell lung cancer patients.

Two steps to determine drug-gene repsonse extraction:
Locate drugs and genes on text.
Fixed number of drugs. 
Genes can be automatically determined using BANNER  Leaman, R. & Gonzalez G. (2008)
Drug-gene response detection
Two baselines:
Co-occurrence: if a drug name and a gene name co-occur in text, they are assumed to be interacting.
Naïve Bayes model: unigram based features will be extracted. 
Example:
Somatic ERCC2 mutations correlate with cisplatin sensitivity in muscle-invasive urothelial carcinoma.

Lucene’s data structures ...


# Hearst Patterns

### Introduction
In this assignment, we will see an interesting application using *regular expressions*.  

[Hypernymy](https://en.wikipedia.org/wiki/Hyponymy_and_hypernymy) (also called is-a relation) is a semantic relation between two noun phrases, hypernym and hyponym, such that the hyponym is a subtype of the hypernym. Also, Hypernym relations are hierarchical, so a word can have multiple hypernyms.  
For example, cat and dog are hyponym of animal because they are types of animals, but they are also a hyponym of mammal.  

Annotating such relations across all possible pairs of words is not feasible. Therefore, many methods have been developed in the last decades in order to automatically construct a database of hypernym relations from large corpora.

A well-established approach to do so is using lexico-syntactic patterns, often called *Hearst patterns*, which detect *key words* like "such as", "like", "especially", "including" and more to find relations using a simple regular expression.

### Construct a database of hypernym relations
```class CreateHypernymDatabase``` receives two arguments: (1) the path to the directory of the corpus and (2) the path to the output file.  
program will read all the files in the directory, find and aggregate hypernym relations that match the Hearst patterns using regular expressions, and save them in a txt file.  

There are a lot of Hearst patterns, in this assignment we will implement partial list:
- ```NP {,} such as NP {, NP, ..., {and|or} NP}.```
- ```such NP as NP {, NP, ..., {and|or} NP}```
- ```NP {,} including NP {, NP, ..., {and|or} NP}```
- ```NP {,} especially NP {, NP, ..., {and|or} NP}```
- ```NP {,} which is {{an example|a kind|a class} of} NP```

The same relation (e.g animal ⟶ dog) can appear using multiple patterns. For each relation, the program will count how many times it appears in overall.  
The format of the file will be as follows:  
```
hypernym: hyponym1 (x), hyponym2 (x) ...
hypernym: hyponym1 (x), hyponym2 (x) ...
...
```
where (x) corresponds to the number of occurrences of the relations (across all possible patterns) in the corpus.

### Hypernym Discovery
```class DiscoverHypernym``` receives 2 arguments: (1) the absolute path to the directory of the corpus and (2) a lemma.  
The program will search all the possible hypernyms of the input lemma and print them to the console as follows:   
```
hypernym1: (x)
hypernym2: (x)
...
```
where (x) corresponds to the number of occurrences of the relations.  
If the input lemma doesn't appear in the corpus, the program will print: ```The lemma doesn't appear in the corpus.```

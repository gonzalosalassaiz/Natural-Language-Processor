# 🧠 Natural Language Processor

> Compiler Front-End for a Simplified JavaScript Language

This repository contains a **Natural Language Processor** project focused on designing and implementing a processor for **JS--**, a simplified version of JavaScript.

The project implements the main stages of a compiler front-end, including **lexical analysis, syntactic analysis, semantic analysis, symbol table management and error handling**, producing intermediate outputs that help validate and visualize the processing of JS-- programs.

The generated parse trees can be visualized using the **VASt** tool, providing a graphical representation of the syntactic structure of the processed input.

---

## 📌 Overview

A language processor transforms source code into structured representations that can be analyzed and validated according to the rules of a programming language.

This project implements a simplified processing pipeline for JS--:

```text
JS-- Source Code
       │
       ▼
┌──────────────────┐
│ Lexical Analysis │
└────────┬─────────┘
         │
         ▼
      Tokens
         │
         ▼
┌────────────────────┐
│ Syntactic Analysis │
└─────────┬──────────┘
          │
          ▼
     Parse Tree
          │
          ▼
┌────────────────────┐
│ Semantic Analysis  │
└─────────┬──────────┘
          │
          ▼
   Symbol Table
          │
          ▼
┌────────────────────┐
│ Error Handling     │
└─────────┬──────────┘
          │
          ▼
      Validation
```

The processor also generates text-based outputs such as token lists, parse information and symbol tables.

---

## 🎯 Objectives

The main objectives of the project are:

* Design and implement a processor for the JS-- language.
* Build a lexical analyzer capable of identifying language tokens.
* Implement syntactic analysis according to the language grammar.
* Generate parse trees representing the syntactic structure of programs.
* Implement semantic analysis for validating program meaning and consistency.
* Maintain a symbol table for identifiers and their associated information.
* Detect and handle errors throughout the processing pipeline.
* Generate structured outputs for debugging and validation.
* Visualize parse trees using the VASt tool.
* Apply fundamental concepts from compiler construction and programming languages.

---

## 🔤 Lexical Analysis

The lexical analyzer processes the JS-- source code and converts it into a sequence of tokens.

The lexer is responsible for identifying elements such as:

* Keywords
* Identifiers
* Literals
* Operators
* Separators
* Delimiters
* Other language-specific symbols

The resulting token stream is stored in a dedicated output file, making it possible to inspect the lexical interpretation of the input program.

```text
Source Code
    │
    ▼
Character Stream
    │
    ▼
Lexical Analyzer
    │
    ▼
┌────────┬────────┬────────┬─────────┐
│ Token  │ Token  │ Token  │   ...   │
└────────┴────────┴────────┴─────────┘
```

---

## 🌳 Syntactic Analysis

The syntactic analyzer validates the token sequence against the grammar defined for JS--.

Its main responsibilities include:

* Parsing the token stream.
* Validating grammatical structures.
* Building the syntactic representation of the program.
* Detecting syntax errors.
* Generating parse-tree information.

The resulting parse structure can be exported and visualized using **VASt**.

```text
Tokens
  │
  ▼
Grammar Rules
  │
  ▼
Parser
  │
  ▼
Parse Tree
  │
  ▼
VASt Visualization
```

---

## 🧠 Semantic Analysis

After syntactic validation, semantic analysis verifies properties that cannot be fully checked by grammar rules alone.

This stage works together with the symbol table to validate the consistency of identifiers and language constructs.

The semantic processing includes concepts such as:

* Identifier management
* Declaration checking
* Symbol resolution
* Scope-related information
* Semantic consistency
* Error detection

This stage demonstrates the transition from syntactic correctness toward meaningful program validation.

---

## 📚 Symbol Table

The processor includes a dedicated **symbol table** used to store and manage information associated with identifiers encountered during analysis.

The symbol table provides a central structure for semantic processing:

```text
Identifier
    │
    ▼
┌──────────────────────┐
│     Symbol Table     │
├──────────────────────┤
│ Name                 │
│ Type / Information   │
│ Scope / Context      │
│ Other Metadata       │
└──────────────────────┘
    │
    ▼
Semantic Analysis
```

A generated `tablaDeSimbolos.txt` file is included in the repository as an example of the processor output.

---

## ⚠️ Error Handling

Error handling is an essential part of the processor and is considered throughout the different analysis stages.

The processor is designed to identify invalid input and report problems encountered during processing, including errors related to:

* Invalid tokens
* Incorrect syntax
* Invalid program structures
* Semantic inconsistencies

Separating the different analysis stages makes it easier to identify the source and nature of processing errors.

---

## 📊 Generated Outputs

The repository contains representative outputs generated by the processor:

| Output | Purpose |
| --- | --- |
| `tokens.txt` | Tokenization results produced by lexical analysis |
| `parse.txt` | Parse-tree / syntactic analysis output |
| `tablaDeSimbolos.txt` | Symbol table generated during processing |
| `input.txt` | Example JS-- source input |

These files make the internal stages of the processor easier to inspect and validate.

---

## 🖥️ Parse Tree Visualization

The **VASt** tool can be used to visualize the parse-tree output generated by the syntactic analyzer.

The overall workflow is:

```text
JS-- Program
     │
     ▼
Lexical Analysis
     │
     ▼
Syntactic Analysis
     │
     ▼
parse.txt
     │
     ▼
     VASt
     │
     ▼
Parse Tree Visualization
```

This provides a visual representation of the grammatical structure produced by the parser.

---

## 🛠️ Technologies

<p align="left">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Compiler%20Construction-2C3E50?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Parsing-34495E?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Language%20Processing-5C6BC0?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/VASt-Visualization-6A1B9A?style=for-the-badge"/>
</p>

### Main Technologies

* **Java** — Core implementation language
* **Lexical Analysis** — Tokenization and source-code processing
* **Syntactic Analysis** — Grammar validation and parse-tree generation
* **Semantic Analysis** — Program consistency validation
* **Symbol Tables** — Identifier and semantic information management
* **VASt** — Parse-tree visualization
* **Text-based Outputs** — Intermediate analysis results

---

## 📁 Repository Structure

```text
Natural-Language-Processor/
│
├── src/
│   └── practica/
│       ├── AnalizadorLexico.java
│       ├── AnalizadorSintactico.java
│       ├── TablaDeSimbolos.java
│       ├── prueba.java
│       └── g.txt
│
├── bin/
│
├── input.txt
├── tokens.txt
├── parse.txt
├── tablaDeSimbolos.txt
└── README.md
```

> **Note:** The repository currently contains generated files and compiled artifacts. A future cleanup could separate source code, generated outputs and build artifacts more clearly.

---

## 🚀 Getting Started

### Prerequisites

Make sure you have installed:

* Java Development Kit (JDK)
* Git
* **VASt**, if you want to visualize the generated parse trees

Clone the repository:

```bash
git clone https://github.com/gonzalosalassaiz/Natural-Language-Processor.git
```

Navigate to the project:

```bash
cd Natural-Language-Processor
```

Compile the Java sources:

```bash
javac -d bin src/practica/*.java
```

Run the corresponding Java entry point from the compiled classes according to the processor configuration.

> **Note:** The current repository does not include a Maven or Gradle build configuration, so compilation is performed directly with the Java compiler.

---

## 🔍 Key Areas Explored

This project combines several fundamental concepts from compiler construction and programming languages:

```text
Source Code
     ↓
Lexical Analysis
     ↓
Token Stream
     ↓
Syntactic Analysis
     ↓
Parse Tree
     ↓
Semantic Analysis
     ↓
Symbol Table
     ↓
Error Handling
     ↓
Processor Outputs
     ↓
VASt Visualization
```

The project provides a practical implementation of the main stages involved in transforming source code into structured representations that can be validated and analyzed.

---

## 📚 Academic Context

This project was developed in an academic context focused on **Programming Languages, Compiler Construction and Language Processing**.

The implementation combines theoretical concepts with practical development of a language processor, covering:

* Formal language processing
* Lexical analysis
* Grammar-based parsing
* Parse-tree construction
* Semantic analysis
* Symbol table design
* Error handling
* Program validation

**Author:** Gonzalo Salas Saiz  
**Degree:** Computer Engineering  
**Project:** Natural Language Processor

---

## 🔮 Future Improvements

Potential future developments include:

* Refactoring class and file names into consistent English naming conventions.
* Introducing a formal build system such as Maven or Gradle.
* Separating source code, generated outputs and compiled artifacts.
* Adding automated tests for lexical, syntactic and semantic analysis.
* Improving error messages with source locations and contextual information.
* Extending semantic validation and scope management.
* Improving the parser architecture and grammar representation.
* Adding a command-line interface for processing arbitrary JS-- files.
* Automating parse-tree generation and VASt visualization.
* Adding CI workflows for compilation and automated testing.
* Providing formal documentation of the JS-- grammar and language specification.

---

## ⭐ About the Project

Natural Language Processor is an academic project that demonstrates the implementation of a simplified programming-language processor, combining **compiler construction, lexical analysis, parsing, semantic analysis and symbol-table management**.

It provides a practical example of how source code can be progressively transformed into tokens, syntactic structures and semantic information while incorporating error handling and visualization tools.

---

<p align="center">
  <i>From source code to structured language — exploring the foundations of compiler construction.</i>
</p>

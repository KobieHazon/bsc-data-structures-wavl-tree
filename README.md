# BSc Data Structures - WAVL Tree

A historical archive of my CS BSc coursework.

## Contents

This repository contains a coauthored Java implementation of a WAVL tree with integer keys and string values.

Implemented operations include:

- Search, insert, and delete.
- Minimum and maximum tracking.
- Sorted key and value array export.
- Order-statistic `select` over the i-th smallest key.
- Rebalancing operation counts for insert/delete.

## Provenance

- Authors: Kobie Hazon and Itzchak Harel.
- Era: CS BSc.
- Last recovered work: 2018 archive copy of a 2017 data structures assignment.
- Assignment baseline: `assignment/wavl-tree-assignment.pdf` is preserved in the first commit.

## Tech Stack

- Java, validated with Java 11 or newer.
- Plain `javac` and `java`; no external dependencies.
- `make` for repeatable compile, test, and cleanup commands.

## Run

```bash
make test
```

To remove generated files:

```bash
make clean
```

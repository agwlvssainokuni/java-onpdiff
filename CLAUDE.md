# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java library implementing two difference algorithms for computing sequence differences:
- **Myers Algorithm**: "An ON(ND) Difference Algorithm" - implemented in `cherry.diff.myers.MyersDiff`
- **Wu Algorithm**: "An ON(NP) Sequence Comparison Algorithm" - implemented in `cherry.diff.wu.WuDiff`

Both algorithms implement the `cherry.diff.Diff` interface and provide functionality to compute edit distance, shortest edit script (SES), and longest common subsequence (LCS) between two sequences.

## Architecture

### Core Interface
- `cherry.diff.Diff` - Main interface defining diff operations for Lists, arrays, strings, and character sequences
- `Diff.Info<T>` - Result container holding edit distance, SES, and LCS
- `Diff.Elem<T>` - Individual diff element with type (SAME, ADD, DEL) and value
- `Diff.Type` - Enum for diff operation types

### Implementations
- `cherry.diff.myers.MyersDiff` - Myers O(ND) algorithm implementation using edit graph traversal
- `cherry.diff.wu.WuDiff` - Wu O(NP) algorithm implementation optimized for shorter sequences

The Wu implementation automatically chooses sequence order (shorter sequence as 'a') for optimal performance and handles normal/reversed cases internally.

## Development Commands

### Build and Test
```bash
./gradlew build          # Build the project
./gradlew test           # Run all tests
./gradlew clean build    # Clean build
```

### Single Test Execution
```bash
./gradlew test --tests "cherry.diff.myers.MyersDiffTest"
./gradlew test --tests "cherry.diff.wu.WuDiffTest"
```

## Project Configuration

- **Java Version**: 17 (source and target compatibility)
- **Build Tool**: Gradle with Wrapper
- **Testing**: JUnit 5 Jupiter platform
- **Dependencies**: Hamcrest matchers, Mockito for testing
- **Encoding**: UTF-8 for all source files
- **IDE Support**: Eclipse plugins configured

## Test Structure

Tests are comprehensive and cover various scenarios:
- Identical sequences
- Complete differences  
- Partial overlaps
- Edge cases (empty sequences, single elements)
- Both algorithm implementations are tested with same test cases for consistency

The project uses Japanese comments in source code, indicating the original author's language preference.

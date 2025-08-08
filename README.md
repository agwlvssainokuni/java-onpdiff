# Java O(ND) & O(NP) Diff Library

A Java library implementing two efficient difference algorithms for computing sequence differences between two sequences.

## Features

This library provides implementations of two well-known difference algorithms:

- **Myers Algorithm**: "An O(ND) Difference Algorithm" - Optimal for general use cases
- **Wu Algorithm**: "An O(NP) Sequence Comparison Algorithm" - Optimized for sequences with shorter differences

Both algorithms implement the same interface and provide:
- Edit distance calculation
- Shortest Edit Script (SES) generation
- Longest Common Subsequence (LCS) extraction
- Support for Lists, arrays, strings, and character sequences

## Requirements

- Java 21 or later
- Gradle (wrapper included)

## Installation

### Gradle

```gradle
dependencies {
    implementation 'cherry:java-onpdiff:1.0.0'
}
```

### Maven

```xml
<dependency>
    <groupId>cherry</groupId>
    <artifactId>java-onpdiff</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Basic Example

```java
import cherry.diff.Diff;
import cherry.diff.myers.MyersDiff;
import cherry.diff.wu.WuDiff;
import java.util.Arrays;
import java.util.List;

public class DiffExample {
    public static void main(String[] args) {
        // Create diff instances
        Diff myersDiff = new MyersDiff();
        Diff wuDiff = new WuDiff();
        
        // Compare two sequences
        List<String> a = Arrays.asList("A", "B", "C", "A", "B", "B", "A");
        List<String> b = Arrays.asList("C", "B", "A", "B", "A", "C");
        
        // Compute difference using Myers algorithm
        Diff.Info<String> result = myersDiff.diff(a, b, String::compareTo);
        
        System.out.println("Edit Distance: " + result.distance());
        System.out.println("LCS Length: " + result.lcs().size());
        
        // Print Shortest Edit Script
        for (Diff.Elem<String> elem : result.ses()) {
            System.out.println(elem.type() + ": " + elem.value());
        }
    }
}
```

### String Comparison

```java
Diff diff = new MyersDiff();
String text1 = "ABCABBA";
String text2 = "CBABAC";

Diff.Info<Character> result = diff.diff(text1, text2);
System.out.println("Edit Distance: " + result.distance());
```

### Array Comparison

```java
Diff diff = new WuDiff();
Integer[] arr1 = {1, 2, 3, 4, 5};
Integer[] arr2 = {1, 3, 4, 6, 5};

Diff.Info<Integer> result = diff.diff(arr1, arr2, Integer::compareTo);
```

## API Reference

### Core Interface

#### `Diff`
Main interface providing difference computation methods:
- `diff(List<T>, List<T>, Comparator<T>)` - Compare two lists
- `diff(T[], T[], Comparator<T>)` - Compare two arrays
- `diff(String, String)` - Compare two strings
- `diff(CharSequence, CharSequence)` - Compare two character sequences

#### `Diff.Info<T>`
Result container providing:
- `distance()` - Edit distance between sequences
- `ses()` - Shortest Edit Script as list of operations
- `lcs()` - Longest Common Subsequence

#### `Diff.Elem<T>`
Individual diff element with:
- `type()` - Operation type (SAME, ADD, DEL)
- `value()` - Element value

#### `Diff.Type`
Enum for operation types:
- `SAME` - Element exists in both sequences
- `ADD` - Element added in second sequence
- `DEL` - Element deleted from first sequence

### Implementations

#### `MyersDiff`
Myers O(ND) algorithm implementation:
- General-purpose difference algorithm
- Optimal for most use cases
- Uses edit graph traversal approach

#### `WuDiff`
Wu O(NP) algorithm implementation:
- Optimized for sequences with shorter differences
- Automatically chooses optimal sequence order
- Better performance when differences are small relative to sequence length

## Algorithm Comparison

| Algorithm | Time Complexity | Space Complexity | Best Use Case |
|-----------|-----------------|------------------|---------------|
| Myers     | O(ND)          | O(D)             | General purpose, longer sequences |
| Wu        | O(NP)          | O(P)             | Short differences, similar sequences |

Where:
- N = length of longer sequence
- D = edit distance (number of differences)
- P = number of deletions

## Building from Source

```bash
# Clone the repository
git clone <repository-url>
cd java-onpdiff

# Build the project
./gradlew build

# Run tests
./gradlew test

# Run specific algorithm tests
./gradlew test --tests "cherry.diff.myers.MyersDiffTest"
./gradlew test --tests "cherry.diff.wu.WuDiffTest"
```

## Testing

The library includes comprehensive tests covering:
- Identical sequences
- Completely different sequences
- Partial overlaps
- Edge cases (empty sequences, single elements)
- Performance comparisons between algorithms

Both algorithm implementations are tested with identical test cases to ensure consistency.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## References

- Myers, Eugene W. "An O(ND) difference algorithm and its variations." Algorithmica 1.1-4 (1986): 251-266. [DOI: 10.1007/BF01840446](https://doi.org/10.1007/BF01840446) | [PDF](http://www.xmailserver.org/diff2.pdf)
- Wu, Sun, et al. "An O(NP) sequence comparison algorithm." Information Processing Letters 35.6 (1990): 317-323. [DOI: 10.1016/0020-0190(90)90035-V](https://doi.org/10.1016/0020-0190(90)90035-V) | [PDF](https://publications.mpi-cbg.de/Wu_1990_6334.pdf)

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

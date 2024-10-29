# Command-Line Interpreter (CLI)

This project is a basic command-line interpreter developed as an assignment for **CS341 - Operating Systems 1 course in Cairo University, Faculty of Computers & Artificial Intelligence**. Written in Java, the CLI supports essential file and directory operations, command output redirection, and utility commands with piping support.

## Features

- **Directory Navigation**:  
  - `pwd`: Displays the current directory path.
  - `cd <dir>`: Changes the current directory.
  - `ls [-a] [-r]`: Lists directory contents with options to show hidden files (`-a`) or reverse the list order (`-r`).

- **File Management**:
  - `mkdir <dir>`: Creates a new directory.
  - `rmdir <dir>`: Removes an empty directory.
  - `touch <file>`: Creates a new, empty file.
  - `mv <source> <destination>`: Moves or renames a file.
  - `cat <file>`: Outputs file contents.

- **Output Redirection**:
  - `> <file>`: Redirects command output to a file, overwriting any existing content.
  - `>> <file>`: Appends command output to a file without overwriting.
  - `| <command>`: Pipes output from one command to another command .

- **Utility Commands**:
  - `help`: Lists all available commands and their descriptions.
  - `exit`: Exits the CLI.

## Testing

The project includes JUnit tests to verify key functions such as `pwd`, `cd`, `mv`, `ls` (with options), output redirection, and piping.

--- 

This CLI provides a foundation for understanding OS command-line operations and file manipulation in Java.

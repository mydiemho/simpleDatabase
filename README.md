Simple Database
==============

An in-memory database similar to Redis that receive commands via standard input (stdin), 
and write responses to standard output (stdout).

#Data Commands

Database accept the following commands:

1. SET name value – Set the variable name to the value value. Neither variable names nor values will contain spaces.
2. GET name – Print out the value of the variable name, or NULL if that variable is not set.
3. UNSET name – Unset the variable name, making it just like that variable was never set.
4. NUMEQUALTO value – Print out the number of variables that are currently set to value. If no variables equal that value, print 0.
5. END – Exit the program. Your program will always receive this as its last command.

#Transaction Commands

In addition to the above data commands, the program also support database transactions with the following commands:

1. BEGIN – Open a new transaction block. Transaction blocks can be nested; a BEGIN can be issued inside of an existing block.
2. ROLLBACK – Undo all of the commands issued in the most recent transaction block, and close the block. Print nothing if successful, or print NO TRANSACTION if no transaction is in progress.
3. COMMIT – Close all open transaction blocks, permanently applying the changes made in them. Print nothing if successful, or print NO TRANSACTION if no transaction is in progress.

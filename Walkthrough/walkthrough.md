# Walkthrough: Compiling and Running Eratos.mj

This walkthrough documents the successful compilation and execution of the `Eratos.mj` MicroJava program.

## Compilation

To compile the program, I used the `MJ.bat` script:

```pwsh
.\MJ.bat Eratos.mj
```

### Result
The compiler generated the following bytecode (truncated for brevity):
```
0: enter 1 1            
3: getstatic 2
6: const 10
...
187: exit
188: return
```
The object file `Eratos.obj` was successfully created.

## Execution

To run the program, I used the `run.bat` script and provided `50` as the upper limit for prime numbers:

```pwsh
echo 50 | .\run.bat Eratos.obj
```

### Result
The program correctly identifies and prints prime numbers up to 50:
```
    2    3    5    7   11
   13   17   19   23   29
   31   37   41   43   47
```

## Summary
The MicroJava environment is correctly configured. You can now use `MJ.bat <filename>.mj` for compilation and `run.bat <filename>.obj` for execution.

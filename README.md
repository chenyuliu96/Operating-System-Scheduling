### CSCI-UA 202 Operating System
Author: Chenyu Liu
Net id: cl3679

Please follow the instruction bellow to execute this program.
1. open a terminal.
2. use cd command to direct to the present working directory that contains this
README.txt.
3. To get detailed output:
  `python3 driver.py <scheduling-style> <input-4.txt> -verbose`

  To get simplified output: `python3 driver.py <scheduling-style> <input-4.txt>`



Note that there is an input directory called "input" that contains all the sample input downloaded from the OS website.


#### Sample Usage
* use "RR" in `<scheduling-style>` to run Round Robin
* use "FCFS" in `<scheduling-style>` to run First Come First Serve
* use "UNI" in `<scheduling-style>` to run Uniprogrammed
* use "SJF" in `<scheduling-style>` to run Shortest Job First


* to see the simple version of Shortest Job First tested with input-4.txt
```python3
python3 driver.py SJF input/input-4.txt
```
* to see the simple version of First Come First Serve tested with input-2.txt
```python3
python3 driver.py FCFS input/input-2.txt
```

* to see the detailed version of Round Robin tested with input-3.txt
```python3
python3 driver.py RR input/input-3.txt -verbose
```

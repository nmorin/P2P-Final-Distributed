#!/bin/bash

echo $"Virginia, 1 (Tracker)"
scp -i nmorin-keypair -r code nmorin@52.5.152.108:

echo $"Ireland, 1"
scp -i nmorin-keypair -r code nmorin@52.17.26.51:

echo $"Singapore, 1"
scp -i nmorin-keypair -r code nmorin@52.74.46.95:

echo $"Tokyo, 1"
scp -i nmorin-keypair -r code nmorin@52.68.126.160:
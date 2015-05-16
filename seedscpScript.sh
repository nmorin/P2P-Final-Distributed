#!/bin/bash

echo $"Virginia, 3"
scp -i nmorin-keypair -r code nmorin@52.7.147.25:

echo $"California, 3"
scp -i nmorin-keypair -r code nmorin@52.8.13.61:

echo $"Oregon, 3"
scp -i nmorin-keypair -r code nmorin@52.11.55.117:

echo $"Ireland, 3"
scp -i nmorin-keypair -r code nmorin@52.17.118.14:

echo $"Sao Paulo, 3"
scp -i nmorin-keypair -r code nmorin@54.94.237.85:

echo $"Tokyo, 3"
scp -i nmorin-keypair -r code nmorin@52.68.70.1:

echo $"Sydney, 3"
scp -i nmorin-keypair -r code nmorin@52.64.32.152:

echo $"Singapore, 3"
scp -i nmorin-keypair -r code nmorin@52.74.137.220:


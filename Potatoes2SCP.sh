#!/bin/bash

echo $"Virginia, 2 (Tracker)"
scp -i nmorin-keypair -r code nmorin@52.7.97.172:

echo $"Ireland, 2"
scp -i nmorin-keypair -r code nmorin@52.16.97.167:

echo $"Sydney, 2"
scp -i nmorin-keypair -r code nmorin@52.64.52.227:

echo $"San Paulo, 2"
scp -i nmorin-keypair -r code nmorin@54.94.219.231:
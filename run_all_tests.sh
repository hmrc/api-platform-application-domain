#!/usr/bin/env bash
sbt clean +scalastyle +compile +coverage +test +coverageReport

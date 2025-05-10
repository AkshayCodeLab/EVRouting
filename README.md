# ⚡ Intelligent EV Routing System

A smart routing solution for electric vehicles (EVs) that ensures optimal travel planning across a road network with charging stations, while minimizing range anxiety and maximizing user satisfaction based on customizable preferences.

## 🚗 Problem Statement

Electric vehicles face a unique set of challenges:

- Limited range between charges (range anxiety)
- Sparse and varied distribution of charging stations
- Need for customized planning based on:
    - Vehicle model & battery capacity
    - Initial charge level
    - User preferences (e.g., minimal detours, maximum energy efficiency)

Existing navigation systems often overlook these critical EV-specific factors, leading to suboptimal routing decisions.

## 🧠 Our Solution

We propose a dynamic EV routing algorithm that:

- **Adapts to vehicle specifications** (battery size, consumption rate)
- **Customizes routes** based on:
    - Current battery level
    - User-defined source and destination
- **Optimizes charging stops**, ensuring:
    - No overcharging or undercharging
    - Balanced energy usage across the route
- **Uses a pair-wise User Preference Matrix**, allowing users to prioritize:
    - Energy Efficiency
    - Detour Minimization
    - Safety Thresholds (e.g., minimum battery % between stops)
    - Refueling Frequency

## 🛠️ Key Features

- Graph-based representation of road networks with:
    - Roads
    - Junctions
    - Charging stations
- Optimal path calculation that:
    - Prevents EV from running out of charge
    - Maintains battery health
    - Honors user-specific travel preferences
- Real-time simulation of routing with visualization

## 🧪 POC Video Demo

> 📽️ **Proof of Concept (POC)** video showcases:
> - Interactive frontend with source/destination inputs
> - Custom vehicle & battery input
> - Dynamic route calculation
> - Visualization of graph traversal and charging logic
> - Preference matrix configuration in real-time

🎥 [Watch the Demo Video](https://drive.google.com/file/d/12mfrDBMILHb9M-z3RpaqTBwGC_TW5AGj/view?usp=sharing)

## 🌐 Frontend Repository

Check out the frontend implementation here:  
🔗 [EV Routing Frontend](https://github.com/AkshayCodeLab/EV_Routing_Frontend)

## 📊 Algorithm Highlights

- Uses **pairwise comparison matrix** (User Preference Matrix) for decision weights
- Combines aspects of:
    - Dijkstra’s Algorithm
    - Multi-criteria decision making (MCDM)
- Scalable for real-time EV fleet routing or individual journey planning


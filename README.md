# DKV 
Key value store written in pure Java

## Design

----
### Diagram
```
                 ┌─────────────────┐                             ┌────────────────┐
                 │                 │                             │                │
                 │                 │      ┌──────────────┐       │                │
                 │                 │      │              │       │                │
                 │                 │      │  In memory   │       │                │
                 │                 ├─────►│  Tree Map    ├──────►│                │
                 │                 │      │              │       │                │
                 │                 │      │              │       │                │
                 │                 │      └─────┬────────┘       │                │
                 │    DB Server    │            │                │    SS Table    │
RESP input──────►│                 │            │                │                │
                 │                 │            ▼                │                │
                 │                 │        ┌─────────┐          │                │
                 │                 │        │         │          │                │
                 │                 │        │  WAL    │          │                │
                 │                 │        │         │          │                │
                 │                 │        │         │          │                │
                 │                 │        └─────────┘          │                │
                 │                 │                             │                │
                 └─────────────────┘                             └────────────────┘
```


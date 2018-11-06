# Unit Prototype

## Design

**UnitExecutor** exposes the public API. `run(Unit)` submits the given Unit to the scheduler; internally, it creates a `UnitSession` for it; then it pulls one UnitSession at a time, and invokes its `fireAllRules()`/`start()`, until no more UnitSessions are available, reaching a steady state.  

**UnitSession** represents an "instance" of a Unit, i.e., a Unit together with all its state. Moreover, a Unit unit may *reference* other units: this is relevant for scheduling (see below). Implementation-wise, it currently wraps a (stateful) unit and transparently manages binding and unbinding of the Unit. To the Executor, Kie unit is invisible.

- For Rule units (currently a distinct PR), there are two `-Session` types, `RuleUnitSession` and `GuardedRuleUnitSession`; the latter only differs from the former, because it holds a set of `Activation` records. Each `RuleUnitSession` also contains a collection `GuarderRuleUnitSessions` that it refers to.

- Process units are basically a different API for a jBPM `ProcessInstance`, which already represent a Process at runtime, together with all its state.

**UnitScheduler** is internally used by the `UnitExecutor` to bring each `UnitSession` to execution, by submitting them to an internal stack-like structure. 

- For each UnitSession, it checks if it contains *references* (se above) to other UnitSessions: if it does, then it schedules such sessions as well.

 - For instance, a RuleUnitSession may *refer* to GuardedRuleUnitSessions (notice this is recursive, because a GuardedRuleUnitSession is a subclass of RuleUnitSession); if it does, and these are `active` (they have >0 Activation records) then it schedules the Guarded unit(s) as well. 

- The *reference* mechanism may be used to e.g. yield to sub-processes in a jBPM process unit


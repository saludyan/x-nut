package nut.jpa.criteria;

public enum XCriteriaCondition {

    eq,             // =
    in,             // in
    notIn,          // not in
    notEq,          // !=
    lessThan,       // <
    lessThanEq,     // <=
    greaterThan,    // >
    greaterThanEq,  // >=

    // String

    like,
    notLike,
    startWith
}

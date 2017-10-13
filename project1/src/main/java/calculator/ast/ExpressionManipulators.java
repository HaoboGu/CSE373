package calculator.ast;

import calculator.interpreter.Environment;
//import calculator.ast.AstNode.ExprType;
import calculator.errors.EvaluationError;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import calculator.gui.ImageDrawer;

/**
 * All of the static methods in this class are given the exact same parameters for
 * consistency. You can often ignore some of these parameters when implementing your
 * methods.
 *
 * Some of these methods should be recursive. You may want to consider using public-private
 * pairs in some cases.
 */
public class ExpressionManipulators {
    /**
     * Takes the given AstNode node and attempts to convert it into a double.
     *
     * Returns a number AstNode containing the computed double.
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if any of the expressions uses an unknown operation.
     */
    public static AstNode toDouble(Environment env, AstNode node) {
        // To help you get started, we've implemented this method for you.
        // You should fill in the TO DO s in the 'toDoubleHelper' method.
        return new AstNode(toDoubleHelper(env.getVariables(), node));
    }

    private static double toDoubleHelper(IDictionary<String, AstNode> variables, AstNode node) {
        // There are three types of nodes, so we have three cases.
        if (node.isNumber()) {
            return node.getNumericValue();
        } else if (node.isVariable()) {
            if (!variables.containsKey(node.getName())) {
                // If the expression contains an undefined variable, we give up.
                throw new EvaluationError("Undefined variable: " + node.getName());
            }
            // System.out.println(variables.get(node.getName()));
            return toDoubleHelper(variables, variables.get(node.getName()));
        } else {
            String name = node.getName();
            if (name.equals("toDouble")){
                return toDoubleHelper(variables, node.getChildren().get(0));
            }
            else if (name.equals("+")) {
                return toDoubleHelper(variables, node.getChildren().get(0)) 
                        + toDoubleHelper(variables, node.getChildren().get(1));
            } else if (name.equals("-")) {
                return toDoubleHelper(variables, node.getChildren().get(0)) 
                        - toDoubleHelper(variables, node.getChildren().get(1));
            } else if (name.equals("*")) {
                return toDoubleHelper(variables, node.getChildren().get(0)) 
                        * toDoubleHelper(variables, node.getChildren().get(1));
            } else if (name.equals("/")) {
                return toDoubleHelper(variables, node.getChildren().get(0)) 
                        / toDoubleHelper(variables, node.getChildren().get(1));
            } else if (name.equals("^")) {
                return Math.pow(toDoubleHelper(variables, node.getChildren().get(0)), 
                        toDoubleHelper(variables, node.getChildren().get(1)));
            } else if (name.equals("negate")) {
                return -toDoubleHelper(variables, node.getChildren().get(0));
            } else if (name.equals("sin")) {
                return Math.sin(toDoubleHelper(variables, node.getChildren().get(0)));
            } else if (name.equals("cos")) {
                return Math.cos(toDoubleHelper(variables, node.getChildren().get(0)));
            } else if (name.equals("cosh")) {
                return Math.cosh(toDoubleHelper(variables, node.getChildren().get(0)));
            } else if (name.equals("sinh")) {
                return Math.sinh(toDoubleHelper(variables, node.getChildren().get(0)));
            }
            else {
                throw new EvaluationError("Unknown operation: " + name);
            }
        }
    }

    public static AstNode simplify(Environment env, AstNode node) {
        // Try writing this one on your own!
        // Hint 1: Your code will likely be structured roughly similarly
        //         to your "toDouble" method
        // Hint 2: When you're implementing constant folding, you may want
        //         to call your "toDouble" method in some way
        if (node.isNumber() || (node.isVariable() && (!env.getVariables().containsKey(node.getName())))) {
            // If the node is number or undefined variable
            return node;
        }
        else if (node.getName().equals("simplify") || node.getName().equals("toDouble")) {              
            return simplify(env, node.getChildren().get(0));
        }
        else if (node.isVariable()) {
            // If the variable is defined by num, return it; else do recursion 
            // Here, variables.get(node)
            return simplify(env, env.getVariables().get(node.getName()));
        }
        else {
            // Operators                                      
            return simplifyOperatorHelper(env, node);
        }
    }
    private static AstNode simplifyMultiply(Environment env, AstNode node) {
        IList<AstNode> newChildren = new DoubleLinkedList<>();
        AstNode left= simplify(env, node.getChildren().get(0));
        AstNode right = simplify(env, node.getChildren().get(1));
        if (left.isNumber() && right.isNumber()) {
            return new AstNode((left.getNumericValue()+right.getNumericValue()));
        }
        else if (left.isNumber() && right.getName().equals(node.getName())) { 
            // node.left is number, node.right is *           
            if (right.getChildren().get(0).isNumber() && !right.getChildren().get(1).isNumber()) {
                // node.right.left is number
                double tmp = left.getNumericValue() * 
                        right.getChildren().get(0).getNumericValue();
                newChildren.add(right.getChildren().get(1));
                newChildren.add(new AstNode(tmp));
            }
            else if (right.getChildren().get(1).isNumber()&& !right.getChildren().get(0).isNumber()) {
                // node.right.right is number
                double tmp = left.getNumericValue() * 
                        right.getChildren().get(1).getNumericValue();
                newChildren.add(right.getChildren().get(0));
                newChildren.add(new AstNode(tmp));             
            }   
            else {
                newChildren.add(left);
                newChildren.add(right);
            }
        }
        else if (right.isNumber() 
                && left.getName().equals(node.getName())) { // node.right is number
            if (left.getChildren().get(0).isNumber() && !left.getChildren().get(1).isNumber()) {
                // node.left.left is number
                double tmp = right.getNumericValue() * 
                        left.getChildren().get(0).getNumericValue();
                newChildren.add(left.getChildren().get(1));
                newChildren.add(new AstNode(tmp));
            }
            else if (left.getChildren().get(1).isNumber() && !left.getChildren().get(0).isNumber()) {
                // node.left.right is number
                double tmp = right.getNumericValue() *
                        left.getChildren().get(1).getNumericValue();
                newChildren.add(left.getChildren().get(0));
                newChildren.add(new AstNode(tmp));             
            }
            else {
                newChildren.add(left);
                newChildren.add(right);
            }
        }
        else {
            newChildren.add(left);
            newChildren.add(right);
        }
        return new AstNode(node.getName(), newChildren);  
    }
    private static AstNode simplifyPlus(Environment env, AstNode node) {
        IList<AstNode> newChildren = new DoubleLinkedList<>();
        AstNode left= simplify(env, node.getChildren().get(0));
        AstNode right = simplify(env, node.getChildren().get(1));
        if (left.isNumber() && right.isNumber()) {
            return new AstNode((left.getNumericValue()+right.getNumericValue()));
        }
        else if (left.isNumber() && right.getName().equals(node.getName())) { 
            // node.left is number            
            if (right.getChildren().get(0).isNumber() && !right.getChildren().get(1).isNumber()) {
                // node.right.left is number
                double tmp = left.getNumericValue() + 
                        right.getChildren().get(0).getNumericValue();
                newChildren.add(right.getChildren().get(1));
                newChildren.add(new AstNode(tmp));
            }
            else if (right.getChildren().get(1).isNumber()&& !right.getChildren().get(0).isNumber()) {
                // node.right.right is number
                double tmp = left.getNumericValue() + 
                        right.getChildren().get(1).getNumericValue();
                newChildren.add(right.getChildren().get(0));
                newChildren.add(new AstNode(tmp));             
            } else {
                newChildren.add(left);
                newChildren.add(right);
            }  
        }
        else if (right.isNumber() && left.getName().equals(node.getName())) { 
            // node.right is number
            if (left.getChildren().get(0).isNumber() && !left.getChildren().get(1).isNumber()) {
                // node.left.left is number
                double tmp = right.getNumericValue() + 
                        left.getChildren().get(0).getNumericValue();
                newChildren.add(left.getChildren().get(1));
                newChildren.add(new AstNode(tmp));
            }
            else if (left.getChildren().get(1).isNumber() && !left.getChildren().get(0).isNumber()) {
                // node.left.right is number
                double tmp = right.getNumericValue() + 
                        left.getChildren().get(1).getNumericValue();
                newChildren.add(left.getChildren().get(0));
                newChildren.add(new AstNode(tmp));             
            }
            else {
                newChildren.add(left);
                newChildren.add(right);
            }
        }
        else {
            newChildren.add(left);
            newChildren.add(right);
        }
        return new AstNode(node.getName(), newChildren); 
    }
    private static AstNode simplifyOperatorHelper(Environment env, AstNode node) {
        // Helper for operators
        String name = node.getName(); // Get operator's name 
        if (name.equals("+") || name.equals("-") || name.equals("*")) {
            if (node.getChildren().get(0).isNumber() 
                    && node.getChildren().get(1).isNumber()) {
                return toDouble(env, node);
            } else {
                if (name.equals("+")) {
                    return simplifyPlus(env, node);
                }
                else if (name.equals("*")) {
                    return simplifyMultiply(env, node);  
                }
                else {
                    IList<AstNode> newChildren = new DoubleLinkedList<>();
                    newChildren.add(simplify(env, node.getChildren().get(0)));
                    newChildren.add(simplify(env, node.getChildren().get(1)));
                    return new AstNode(name, newChildren);
                }
            }                               
        }
        else if (name.equals("/") || name.equals("^")) {
            IList<AstNode> newChildren = new DoubleLinkedList<>();
            newChildren.add(simplify(env, node.getChildren().get(0)));
            newChildren.add(simplify(env, node.getChildren().get(1)));
            return new AstNode(name, newChildren);
        }  
        else if (name.equals("cos") || name.equals("sin") || name.equals("negate") ||
                name.equals("cosh") || name.equals("sinh")) {
            IList<AstNode> newChildren = new DoubleLinkedList<>();
            newChildren.add(simplify(env, node.getChildren().get(0)));
            return new AstNode(name, newChildren);
        }
        else {
            throw new EvaluationError("EvaluationError: Symbol not defined");
        }
    }
        

    /**
     * Expected signature of plot:
     *
     * >>> plot(exprToPlot, var, varMin, varMax, step)
     *
     * Example 1:
     *
     * >>> plot(3 * x, x, 2, 5, 0.5)
     *
     * This command will plot the equation "3 * x", varying "x" from 2 to 5 in 0.5
     * increments. In this case, this means you'll be plotting the following points:
     *
     * [(2, 6), (2.5, 7.5), (3, 9), (3.5, 10.5), (4, 12), (4.5, 13.5), (5, 15)]
     *
     * ---
     *
     * Another example: now, we're plotting the quadratic equation "a^2 + 4a + 4"
     * from -10 to 10 in 0.01 increments. In this case, "a" is our "x" variable.
     *
     * >>> c := 4
     * 4
     * >>> step := 0.01
     * 0.01
     * >>> plot(a^2 + c*a + a, a, -10, 10, step)
     *
     * ---
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if varMin > varMax
     * @throws EvaluationError  if 'var' was already defined
     * @throws EvaluationError  if 'step' is zero or negative
     */
    
    private static void drawImage(Environment env, AstNode exp, 
            AstNode var, double varMin, double varMax, double step) {
        IDictionary<String, AstNode> variables = env.getVariables();
        ImageDrawer drawer = env.getImageDrawer();
        IList<Double> xValues = new DoubleLinkedList<>();
        IList<Double> yValues = new DoubleLinkedList<>();
        for (double v = varMin; v <= varMax; v = v + step) {
            variables.put(var.getName(), new AstNode(v));
            double yValue = toDoubleHelper(variables, exp);
            xValues.add(v);
            yValues.add(yValue);
        }
        drawer.drawScatterPlot("plot", "x", "output", xValues, yValues);
        variables.remove(var.getName());
    }
    public static AstNode plot(Environment env, AstNode node) {
        IDictionary<String, AstNode> variables = env.getVariables();
        // First, check whether var is defined
        if (variables.containsKey(node.getChildren().get(1).getName())) {   
            throw new EvaluationError("EvaluationError: var was already defined");           
        }
        
        // Var is not defined, give var a value to check expr
        variables.put(node.getChildren().get(1).getName(), new AstNode(0));
        //  Check expr
        try {          
            toDoubleHelper(variables, node.getChildren().get(0));
        } catch (EvaluationError ex) {
            throw new EvaluationError("EvaluationError: expr contains undefined variables");
        }
        variables.remove(node.getChildren().get(1).getName()); // Remove var in dictionary
        
        // Check varMin, varMax, step
        double varMin = 0;
        double varMax = 0;
        double step = 0;
        try {
            varMin = toDoubleHelper(variables, node.getChildren().get(2));
            varMax = toDoubleHelper(variables, node.getChildren().get(3));
            step = toDoubleHelper(variables, node.getChildren().get(4));
        }
        catch (EvaluationError ex) {
            throw new EvaluationError("EvaluationError: paras contains undefined variables");
        }
        
        if (varMin > varMax) {
            throw new EvaluationError("EvaluationError: varMin > varMax");
        }
        else if (step <= 0) {
            throw new EvaluationError("EvaluationError: step is zerp or negative");
        }
        else {
            drawImage(env, node.getChildren().get(0), node.getChildren().get(1), 
                    varMin, varMax, step);           
        }
        // Note: every single function we add MUST return an
        // AST node that your "simplify" function is capable of handling.
        // However, your "simplify" function doesn't really know what to do
        // with "plot" functions (and what is the "plot" function supposed to
        // evaluate to anyways?) so we'll settle for just returning an
        // arbitrary number.
        //
        // When working on this method, you should uncomment the following line:
        //
        return new AstNode(1);
    }
}

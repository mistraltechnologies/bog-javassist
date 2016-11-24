# bog-javassist
Javassist-based runtime code generator extension for BOG.
## Summary
Builder classes based on the [BOG Builder library](https://github.com/mistraltechnologies/bog) are
typically boiler-plate code.

To avoid the burden of writing
these, static code generators such as the [Bogen IntelliJ plugin](https://github.com/mistraltechnologies/bogen) can
be used. However, this still results in a lot of code that is baggage in a project.

Bog-Javassist is an extension to BOG that removes the need for boiler-plate code without
resorting to a reflection-oriented solution, so that the benefits of statically typed code are retained: type-safety
and IDE-supported code completion and refactorings. It allows builders to be written as interfaces only, with the
implementation being generated at runtime using [Javassist](http://www.javassist.org "javassist.org").

## Usage Examples

Begin by creating an interface containing the builder methods you want:

    @Builds(value = Person.class, description = "a Person")
    public interface PersonBuilder {
    
// TODO - fill out examples


For more involved usage examples, see the tests in the library source code tree.  

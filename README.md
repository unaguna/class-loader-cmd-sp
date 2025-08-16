# Class Loader Command

class-loader-cmd shows data in the classpath. It requires Java>=8.

For example:

```shell
# shows classes in the classpath 'a.jar:/path/to/classpath/dir/'
java -jar class-loader-cmd.jar ls-classes -cp a.jar:/path/to/classpath/dir/
```

For more information, see --help.

```shell
java -jar class-loader-cmd.jar --help
```

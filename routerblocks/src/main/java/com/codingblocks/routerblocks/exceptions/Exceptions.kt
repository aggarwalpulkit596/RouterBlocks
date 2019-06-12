package com.codingblocks.routerblocks.exceptions

class InvalidValueTypeException(path: String, value: String) : RuntimeException(
    String.format(
        "The type of the value is not match witch the path, Path : %s, value: %s",
        path,
        value
    )
)

class RouteNotFoundException(routePath: String) :
    Exception(String.format("The route not found: %s", routePath))


class InvalidRoutePathException(routePath: String) :
    Exception(String.format("Invalid route path %s", routePath))



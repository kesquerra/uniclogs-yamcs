## Datatypes:

datatypes object containing array of data types

required fields:

    - name: name string
    - type: (int, bool, float, enum, string, time)

optional fields

    - size: int value referring to size in bits
      - default value: 8
    - byte_order: (little_endian, big_endian)
      - default value: little_endian
    - encoding (signed, unsigned)
      - default value: unsigned

Example:

```json
"datatypes": [
        {
            "name": "uint8",
            "type": "int"
        },
        {
            "name": "int32",
            "type": "int",
            "size": 32,
            "encoding": "signed"
        },
        {
            "name": "bool_toggle",
            "type": "bool",
            "size": 2
        }
    ]
```

## Arguments:

arguments object containing array of arguments

required fields:

    - name: name string
    - datatype: name string referring to previously declared data type


optional fields:
    - args: additional arguments pertaining to specific data type
      - ex: bool datatype can have one_label and zero_label referring to string names for values

Example:

```json
"arguments": [
        {
            "name": "bool_enable_disable",
            "datatype": "bool_toggle",
            "args": {
                "zero_label": "disable",
                "one_label": "enable"
            }
        }
    ]
```

## Commands:

commands object containing array of commands

required fields:

    - name: name string
    - code: bit value to send in command

optional fields:

    - arguments: argument name string referring to previously declared argument
      - default: no arguments
    - response: boolean referring to if the command requires waiting for a response
      - default value: false
    - type: enum value pertaining to specification (could be workshopped)
      - ex: edl refers to needs response, and listens on edl datalink


```json
"commands": [
        {
            "name": "C3TxCtrl",
            "code": 0,
            "arguments": [
                "bool_enable_disable"
            ],
            "response": true
        }
    ]
```


## Full Example

```json
{
    "datatypes": [
        {
            "name": "uint8",
            "type": "int"
        },
        {
            "name": "int32",
            "type": "int",
            "size": 32,
            "encoding": "signed"
        },
        {
            "name": "bool_toggle",
            "type": "bool",
            "size": 2
        }
    ],
    "arguments": [
        {
            "name": "bool_enable_disable",
            "datatype": "bool_toggle",
            "args": {
                "zero_label": "disable",
                "one_label": "enable"
            }
        }
    ],
    "commands": [
        {
            "name": "C3TxCtrl",
            "code": 0,
            "arguments": [
                "bool_enable_disable"
            ],
            "response": true
        }
    ]
}
```
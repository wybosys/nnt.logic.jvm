// package: com.test
// file: dao.proto

/* tslint:disable */
/* eslint-disable */

import * as jspb from "google-protobuf";

export class Echoo extends jspb.Message { 
    getId(): number;
    setId(value: number): Echoo;
    getInput(): string;
    setInput(value: string): Echoo;
    getOutput(): string;
    setOutput(value: string): Echoo;

    serializeBinary(): Uint8Array;
    toObject(includeInstance?: boolean): Echoo.AsObject;
    static toObject(includeInstance: boolean, msg: Echoo): Echoo.AsObject;
    static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
    static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
    static serializeBinaryToWriter(message: Echoo, writer: jspb.BinaryWriter): void;
    static deserializeBinary(bytes: Uint8Array): Echoo;
    static deserializeBinaryFromReader(message: Echoo, reader: jspb.BinaryReader): Echoo;
}

export namespace Echoo {
    export type AsObject = {
        id: number,
        input: string,
        output: string,
    }
}

export class Echoos extends jspb.Message { 
    clearItemList(): void;
    getItemList(): Array<Echoo>;
    setItemList(value: Array<Echoo>): Echoos;
    addItem(value?: Echoo, index?: number): Echoo;

    serializeBinary(): Uint8Array;
    toObject(includeInstance?: boolean): Echoos.AsObject;
    static toObject(includeInstance: boolean, msg: Echoos): Echoos.AsObject;
    static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
    static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
    static serializeBinaryToWriter(message: Echoos, writer: jspb.BinaryWriter): void;
    static deserializeBinary(bytes: Uint8Array): Echoos;
    static deserializeBinaryFromReader(message: Echoos, reader: jspb.BinaryReader): Echoos;
}

export namespace Echoos {
    export type AsObject = {
        itemList: Array<Echoo.AsObject>,
    }
}

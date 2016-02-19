# TLV
tlv编码的java实现，和网上的有点不同<br>
和网上的不同主要是Tag编码不同：
网上的TLV中的Tag编码<br>
1) Tag首节字说明
    第6~7位：表示TLV的类型，00表示TLV描述的是基本数据类型(Primitive Frame, int,string,long...)，01表示用户自定义类型(Private Frame，常用于描述协议中的消息)。<br>
    第5位：表示Value的编码方式，分别支持Primitive及Constructed两种编码方式, Primitive指以原始数据类型进行编码，Constructed指以TLV方式进行编码，0表示以Primitive方式编码，1表示以Constructed方式编码。<br>
    第0~4位：当Tag Value小于0x1F(31)时，首字节0～4位用来描述Tag Value，否则0~4位全部置1，作为存在后续字节的标志，Tag Value将采用后续字节进行描述。<br>
2) Tag后续字节说明
    后续字节采用每个字节的0～6位（即7bit）来存储Tag Value, 第7位用来标识是否还有后续字节。<br>
    第7位：描述是否还有后续字节，1表示有后续字节，0表示没有后续字节，即结束字节。<br>
    第0~6位：填充Tag Value的对应bit(从低位到高位开始填充)，如：Tag Value为：0000001 11111111 11111111 (10进制：131071), 填充后实际字节内容为：10000111 11111111 01111111。<br>

我们的TLV编码的Tag编码在Tag首字节的第7位如果置为1表示有后续字节，0表示无后续字节，并且在Value大于0x1F(31)时第0~4位全部置为0



	.file	"JNITest.c"
	.text
.globl _Java_JNITest_fileCopy
	.def	_Java_JNITest_fileCopy;	.scl	2;	.type	32;	.endef
_Java_JNITest_fileCopy:
	pushl	%ebp
	movl	%esp, %ebp
	subl	$24, %esp
	movl	16(%ebp), %eax
	movl	%eax, (%esp)
	call	_puts
	leave
	ret
	.def	_puts;	.scl	2;	.type	32;	.endef

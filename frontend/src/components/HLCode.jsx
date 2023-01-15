import React from 'react';

import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { oneLight } from 'react-syntax-highlighter/dist/esm/styles/prism';

export default function HLCode({ code }) {
  return (
    <SyntaxHighlighter language="json" style={ oneLight }
      className="syntaxhighlighter"
    >
      { code }
    </SyntaxHighlighter>
  )
}